package com.stoq.service;

import com.stoq.domain.Estoque;
import com.stoq.domain.Funcionario;
import com.stoq.domain.MovimentacaoEstoque;
import com.stoq.domain.Pedido;
import com.stoq.domain.PedidoItens;
import com.stoq.dto.EstoqueDTO.EstoqueResponseDTO;
import com.stoq.mapper.EstoqueMapper;
import com.stoq.repository.EstoqueRepository;
import com.stoq.repository.HistoricoEstoqueRepository;
import com.stoq.repository.MovimentacaoEstoqueRepository;
import com.stoq.repository.PedidoRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList; // Adicionado import que estava faltando
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final HistoricoEstoqueRepository historicoRepository;
    private final PedidoRepository pedidoRepository;
    private final FuncionarioService funcionarioService;

    private final AlertaService alertaService;

    public EstoqueService(
            EstoqueRepository estoqueRepository,
            MovimentacaoEstoqueRepository movimentacaoRepository,
            HistoricoEstoqueRepository historicoRepository,
            PedidoRepository pedidoRepository,
            FuncionarioService funcionarioService,
            @Lazy AlertaService alertaService) {

        this.estoqueRepository = estoqueRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.historicoRepository = historicoRepository;
        this.pedidoRepository = pedidoRepository;
        this.funcionarioService = funcionarioService;
        this.alertaService = alertaService;
    }

    /**
     * Retirada de materiais do estoque, registrando Movimentacao e atualizando saldo.
     */
    @Transactional
    public void retiradaEstoque(Long laboratorioId, Long funcionarioId, Long qrcodeId, List<PedidoItens> materiaisRetirados) {

        List<Long> faltantes = new ArrayList<>();

        for (PedidoItens item : materiaisRetirados) {
            Long materialId = item.getId().getIdMaterial();
            BigDecimal qtde = item.getQtdeSolicitada();

            BigDecimal saldoAtual = estoqueRepository.findQuantidadeAtual(laboratorioId, materialId);

            if (saldoAtual == null) {
                saldoAtual = BigDecimal.ZERO;
            }

            if (saldoAtual.compareTo(qtde) < 0) {
                faltantes.add(materialId);
                System.out.println("⚠ Estoque insuficiente para material ID " + materialId);
                continue;
            }

            estoqueRepository.atualizarEstoque(laboratorioId, materialId, qtde.negate());

            MovimentacaoEstoque mov = new MovimentacaoEstoque(
                    null,
                    LocalDate.now(),
                    laboratorioId,
                    materialId,
                    "SAIDA",
                    qtde,
                    qrcodeId,
                    funcionarioId,
                    "Retirada via QRCode"
            );
            movimentacaoRepository.save(mov);
        }

        if (!faltantes.isEmpty()) {
            System.out.println("Gerando pedido automático para materiais faltantes...");
            alertaService.monitorarBaixaEstoque(laboratorioId);
        }

        alertaService.monitorarBaixaEstoque(laboratorioId);
    }


    /**
     * Entrada de estoque via recebimento de pedido.
     */
    @Transactional
    public void entradaEstoquePorPedido(Long pedidoId, Long recebedorId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado: " + pedidoId));

        Funcionario recebedor = funcionarioService.buscarPorId(recebedorId);

        Long laboratorioId = pedido.getIdLaboratorio();

        List<PedidoItens> itens = pedidoRepository.buscarItensDoPedido(pedidoId);

        Map<Long, BigDecimal> somaPorMaterial = itens.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getId().getIdMaterial(),
                        Collectors.mapping(PedidoItens::getQntdeRecebida,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        for (Map.Entry<Long, BigDecimal> entry : somaPorMaterial.entrySet()) {
            Long materialId = entry.getKey();
            BigDecimal qtde = entry.getValue();

            estoqueRepository.atualizarEstoque(laboratorioId, materialId, qtde);

            MovimentacaoEstoque mov = new MovimentacaoEstoque(
                    null,
                    LocalDate.now(),
                    laboratorioId,
                    materialId,
                    "ENTRADA",
                    qtde,
                    null,
                    recebedor.getIdFuncionario(),
                    "Entrada consolidada de pedido"
            );
            movimentacaoRepository.save(mov);
        }

        pedido.setStatus("RECEBIDO");
        pedidoRepository.save(pedido);

        alertaService.monitorarBaixaEstoque(laboratorioId);
    }

    /**
     * Consolidar o histórico de estoque
     */
    @Transactional
    public void consolidarHistoricoDiario() {
        LocalDate dia = LocalDate.now();
        historicoRepository.copiarEstoqueParaHistorico(dia);
    }

    /**
     * Lista todo o estoque (último saldo) de um laboratório, ordenado por data.
     * RETORNA LISTA DE DTOs.
     */
    @Transactional(readOnly = true)
    public List<EstoqueResponseDTO> listarEstoquePorLaboratorio(Long laboratorioId) {
        List<Estoque> entidadesEstoque = estoqueRepository.findByIdLabOrderByDiaDesc(laboratorioId);
        return EstoqueMapper.toResponseDTOList(entidadesEstoque); // Mapeamento para DTO
    }
}