package com.stoq.service;

import com.stoq.domain.Funcionario;
import com.stoq.domain.Material;
import com.stoq.domain.Pedido;
import com.stoq.domain.PedidoItens;
import com.stoq.domain.PedidoItensID;
import com.stoq.dto.PedidoDTO.PedidoRecebimentoRequestDTO;
import com.stoq.dto.PedidoDTO.PedidoResponseDTO;
import com.stoq.mapper.PedidoMapper;
import com.stoq.repository.MaterialRepository;
import com.stoq.repository.PedidoRepository;
import com.stoq.repository.PedidoItensRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço que realiza e finaliza pedidos
 * Status: PENDENTE, RECEBIDO, CANCELADO.
 */
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoItensRepository pedidoItensRepository;
    private final MaterialRepository materialRepository;
    private final EstoqueService estoqueService;
    private final FuncionarioService funcionarioService;

    public PedidoService(PedidoRepository pedidoRepository,
                         PedidoItensRepository pedidoItensRepository,
                         MaterialRepository materialRepository,
                         EstoqueService estoqueService,
                         FuncionarioService funcionarioService) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoItensRepository = pedidoItensRepository;
        this.materialRepository = materialRepository;
        this.estoqueService = estoqueService;
        this.funcionarioService = funcionarioService;
    }

    // =================== MÉTODOS DE ESCRITA (CRIAÇÃO, ATUALIZAÇÃO, AÇÃO) ===================

    /**
     * Acionado pelo AlertaService. Cria cabeçalho em PEDIDOS (PENDENTE) + itens sugeridos em PEDIDO_ITENS.
     *
     * @param materiaisEmBaixa IDs de materiais abaixo do mínimo
     * @param solicitanteId    ID do funcionário solicitante
     * @param idLaboratorio    laboratório do pedido
     * @return ID do pedido criado
     */
    @Transactional
    public Long realizarPedido(List<Long> materiaisEmBaixa, Long solicitanteId, Long idLaboratorio) {
        Funcionario solicitante = funcionarioService.buscarPorId(solicitanteId);
        validarSolicitante(solicitante);

        if (materiaisEmBaixa == null || materiaisEmBaixa.isEmpty())
            throw new IllegalArgumentException("Não há materiais em baixa para gerar pedido.");

        // 1. Monta itens sugeridos
        List<PedidoItens> itens = new ArrayList<>();
        for (Long idMat : materiaisEmBaixa) {
            Material m = materialRepository.findById(idMat).orElse(null);
            if (m == null) continue;

            BigDecimal qtdSugerida = calcularQtdCompraSugerida(m);

            PedidoItens item = new PedidoItens(
                    null,
                    idMat,
                    qtdSugerida,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    null,
                    null
            );
            itens.add(item);
        }
        if (itens.isEmpty())
            throw new IllegalArgumentException("IDs de materiais inválidos; não foi possível montar o pedido.");

        // 2. Cria cabeçalho do pedido
        Pedido cabecalho = new Pedido(
                null,
                gerarNumero(),
                idLaboratorio,
                solicitanteId,
                "PENDENTE",
                LocalDate.now(),
                null,
                null,
                null
        );

        cabecalho = pedidoRepository.save(cabecalho);
        Long idPedido = cabecalho.getIdPedido();

        // 3. Persiste itens em PEDIDO_ITENS
        for (PedidoItens it : itens) {
            // Cria a chave composta com o ID do pedido recém-gerado
            it.setId(new PedidoItensID(idPedido, it.getId().getIdMaterial()));

            System.out.printf(
                    "[DEBUG] Inserindo PedidoItens -> Pedido_ID: %d | Material_ID: %d | QtdeSolicitada: %s%n",
                    idPedido,
                    it.getId().getIdMaterial(),
                    it.getQtdeSolicitada().toString()
            );

            pedidoItensRepository.save(it);
        }

        return idPedido;
    }

    /**
     * "Aprovar/Enviar": define o fornecedor e mantém status PENDENTE.
     */
    @Transactional
    public void enviarPedido(Long idPedido, Long gestorId, String fornecedorNome) {
        Funcionario gestor = funcionarioService.buscarPorId(gestorId);
        validarGestor(gestor);

        if (fornecedorNome == null || fornecedorNome.isBlank())
            throw new IllegalArgumentException("Informe fornecedor_nome.");

        Pedido p = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado."));

        if (!"PENDENTE".equalsIgnoreCase(p.getStatus()))
            throw new IllegalStateException("Somente pedidos PENDENTE podem ser enviados.");

        p.setFornecedorNome(fornecedorNome);
        pedidoRepository.save(p);
        System.out.println("Pedido enviado ao fornecedor com sucesso!");
    }

    /**
     * Edição completa dos itens antes do envio (substituição total).
     */
    @Transactional
    public void editarItens(Long idPedido, Long gestorId, List<PedidoItens> novosItens) {
        Funcionario gestor = funcionarioService.buscarPorId(gestorId);
        validarGestor(gestor);

        if (novosItens == null || novosItens.isEmpty())
            throw new IllegalArgumentException("Lista de itens não pode ser vazia.");

        Pedido p = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado."));

        if (!"PENDENTE".equalsIgnoreCase(p.getStatus()))
            throw new IllegalStateException("Somente pedidos PENDENTE podem ser editados.");

        // 1. Deleta itens atuais (usando a lista do relacionamento @OneToMany)
        pedidoItensRepository.deleteAll(p.getItens());

        // 2. Insere substitutos
        for (PedidoItens it : novosItens) {
            // Garante que a chave composta seja configurada corretamente
            it.setId(new PedidoItensID(idPedido, it.getId().getIdMaterial()));
            pedidoItensRepository.save(it);
        }
    }

    /**
     * Recebimento do pedido: atualiza itens, marca cabeçalho como RECEBIDO e chama EstoqueService.
     *
     * @param recebimentos mapa material_id -> DTO de dados recebidos
     */
    @Transactional
    public void receberPedido(Long idPedido,
                              Long recebedorId,
                              Map<Long, PedidoRecebimentoRequestDTO> recebimentos) {
        Funcionario recebedor = funcionarioService.buscarPorId(recebedorId);
        validarRecebedor(recebedor);

        Pedido p = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado."));

        if (!"PENDENTE".equalsIgnoreCase(p.getStatus()))
            throw new IllegalStateException("Somente pedidos PENDENTE podem ser recebidos.");

        // 1. Atualiza itens conforme o que chegou
        List<PedidoItens> itensAtuais = pedidoItensRepository.findByIdIdPedido(idPedido);
        Map<Long, PedidoItens> porMaterial = itensAtuais.stream()
                .collect(Collectors.toMap(item -> item.getId().getIdMaterial(), item -> item));

        for (Map.Entry<Long, PedidoRecebimentoRequestDTO> e : recebimentos.entrySet()) {
            Long idMat = e.getKey();
            PedidoRecebimentoRequestDTO dadosRecebidos = e.getValue();

            PedidoItens original = porMaterial.get(idMat);
            if (original == null) {
                throw new NoSuchElementException("Material " + idMat + " não pertence a este pedido.");
            }

            // Uso direto dos BigDecimals e LocalDate do DTO
            original.setQntdeRecebida(naoNegativo(dadosRecebidos.getQntdeRecebida()));
            original.setLote(dadosRecebidos.getLote());
            original.setValidade(dadosRecebidos.getValidade());
            original.setPrecoUnitario(naoNegativo(dadosRecebidos.getPrecoUnitario()));

            pedidoItensRepository.save(original);
        }

        // 2. Marca cabeçalho como RECEBIDO
        p.setStatus("RECEBIDO");
        p.setDtRecebimento(LocalDate.now());
        pedidoRepository.save(p);

        // 3. Atualiza estoque/movimentação
        estoqueService.entradaEstoquePorPedido(idPedido, recebedorId);
    }

    /**
     * Cancelamento de pedido.
     */
    @Transactional
    public void cancelarPedido(Long idPedido, Long funcionarioId) {
        Funcionario gestorOuAdmin = funcionarioService.buscarPorId(funcionarioId);
        validarGestorOuAdmin(gestorOuAdmin);

        Pedido p = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado."));

        if (!"PENDENTE".equalsIgnoreCase(p.getStatus()))
            throw new IllegalStateException("Somente pedidos PENDENTE podem ser cancelados.");

        p.setStatus("CANCELADO");
        pedidoRepository.save(p);
    }

    // =================== MÉTODOS DE LEITURA (CRUD READ) ===================

    /**
     * Busca um pedido pelo ID e mapeia para DTO.
     */
    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorId(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new NoSuchElementException("Pedido " + idPedido + " não encontrado."));

        // O PedidoMapper deve ser usado para transformar a Entidade em DTO
        return PedidoMapper.toResponseDTO(pedido);
    }

    /**
     * Lista todos os pedidos e mapeia para DTOs.
     */
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll().stream()
                .map(PedidoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // =================== Funções de Suporte (Helpers) ===================

    private BigDecimal calcularQtdCompraSugerida(Material m) {
        Integer min = m.getEstoqueMinimo();
        int base = (min != null ? min : 0);
        return BigDecimal.valueOf(Math.max(base, 1));
    }

    private BigDecimal naoNegativo(BigDecimal v) {
        return v.max(BigDecimal.ZERO);
    }

    private void validarSolicitante(Funcionario f) {
        if (f == null) throw new NoSuchElementException("Solicitante inválido ou não encontrado.");
    }

    private void validarGestor(Funcionario f) {
        if (f == null) throw new NoSuchElementException("Gestor inválido ou não encontrado.");
        if (!"GESTOR".equalsIgnoreCase(f.getCargo()))
            throw new SecurityException("Apenas GESTOR pode aprovar/editar/enviar pedidos.");
    }

    private void validarRecebedor(Funcionario f) {
        if (f == null) throw new NoSuchElementException("Recebedor inválido ou não encontrado.");
        if (!("ADMIN".equalsIgnoreCase(f.getCargo()) || "ALMOX".equalsIgnoreCase(f.getCargo()) || "GESTOR".equalsIgnoreCase(f.getCargo()))) {
            throw new SecurityException("Cargo não autorizado para receber pedido.");
        }
    }

    private void validarGestorOuAdmin(Funcionario f) {
        if (f == null) throw new NoSuchElementException("Funcionário inválido ou não encontrado.");
        if (!("GESTOR".equalsIgnoreCase(f.getCargo()) || "ADMIN".equalsIgnoreCase(f.getCargo())))
            throw new SecurityException("Apenas GESTOR ou ADMIN podem cancelar pedido.");
    }

    private String gerarNumero() {
        return "PED-" + LocalDate.now() + "-" + System.currentTimeMillis();
    }
}