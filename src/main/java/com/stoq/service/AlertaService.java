package com.stoq.service;

import com.stoq.domain.Funcionario;
import com.stoq.domain.Material;
import com.stoq.repository.MaterialRepository;
import com.stoq.repository.EstoqueRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável por monitorar estoque e validade de materiais,
 * disparando alertas e acionando pedidos automáticos quando necessário
 */
@Service
public class AlertaService {

    private final EstoqueRepository estoqueRepository;
    private final MaterialRepository materialRepository;

    // Injeção de outros Services necessários para a ação (PedidoService)
    private final PedidoService pedidoService;
    private final FuncionarioService funcionarioService;

    // Configuração do aviso de validade em dias (pode vir de um arquivo de config)
    private static final int DIAS_PADRAO_AVISO_VALIDADE = 14;

    public AlertaService(
            EstoqueRepository estoqueRepository,
            MaterialRepository materialRepository,
            @Lazy PedidoService pedidoService, // Pode haver ciclo, injetar com @Lazy
            FuncionarioService funcionarioService) {

        this.estoqueRepository = estoqueRepository;
        this.materialRepository = materialRepository;
        this.pedidoService = pedidoService;
        this.funcionarioService = funcionarioService;
    }

    /**
     * Sobrecarga: dispara alerta usando um usuário "SISTEMA" (Funcionario ID 29)
     * Após preencher o solicitante como sistema, ele chama o monitorarBaixaEstoque que possui toda a lógica de monitoração
     */
    public void monitorarBaixaEstoque(Long laboratorioId) {
        // Busca o ID do funcionário "SISTEMA" que você definiu como 29L
        // Em um sistema real, este ID seria buscado pelo cargo ou configurado.
        Long funcionarioSistemaId = 29L;

        monitorarBaixaEstoque(laboratorioId, funcionarioSistemaId);
    }

    // Sobrecarga para o AlertaService ser chamado pelo EstoqueService (sem Funcionario)
    public void monitorarBaixaEstoque(Long laboratorioId, Long funcionarioId) {
        // Busca o funcionário real para validação (se o serviço for acionado por um usuário)
        Funcionario solicitante = funcionarioService.buscarPorId(funcionarioId);

        monitorarBaixaEstoque(laboratorioId, solicitante);
    }


    /**
     * Monitora estoques e dispara pedidos quando o saldo atual está abaixo do mínimo
     * @param laboratorioId ID do laboratório
     * @param solicitante Usuário que disparou (ADMIN, ALMOX, SISTEMA, etc)
     */
    @Transactional(readOnly = true) // Embora chame um método @Transactional de outro Service, esta lógica é apenas de leitura/decisão.
    public void monitorarBaixaEstoque(Long laboratorioId, Funcionario solicitante) {

        // Uso do método customizado que criamos no EstoqueRepository
        List<Material> materiaisCriticos = materialRepository.findMateriaisAbaixoEstoqueMinimo(laboratorioId);

        if (materiaisCriticos.isEmpty()) {
            System.out.println("Estoque dentro dos limites mínimos.");
            return;
        }

        // Log/alerta
        for (Material m : materiaisCriticos) {
            System.out.println("⚠ Alerta: Material abaixo do mínimo → " + m.getNome());
        }

        // Monta lista de IDs para pedido automático
        List<Long> idsMateriais = materiaisCriticos.stream()
                .map(Material::getIdMaterial)
                .collect(Collectors.toList());

        // Chama PedidoService para abrir pedido (método que é @Transactional)
        Long idPedido = pedidoService.realizarPedido(idsMateriais, solicitante.getIdFuncionario(), laboratorioId);
        System.out.println("\nPedido automático criado: ID " + idPedido +"\n");

        // Chama monitoramento de validade logo após o alerta de estoque
        monitorarValidade(laboratorioId, DIAS_PADRAO_AVISO_VALIDADE);
    }

    /**
     * Monitora validade dos itens (puxando da tabela PEDIDO_ITENS)
     * Dispara alertas caso o prazo esteja próximo do vencimento
     * @param laboratorioId ID do laboratório
     * @param diasAviso Prazo de alerta em dias
     */
    @Transactional(readOnly = true)
    public void monitorarValidade(Long laboratorioId, int diasAviso) {

        // Uso do método customizado que criamos no EstoqueRepository
        List<Material> materiaisVencendo = estoqueRepository.findMateriaisProximosVencimento(laboratorioId, diasAviso);

        if (materiaisVencendo.isEmpty()) {
            System.out.println("Nenhum material próximo do vencimento.");
            return;
        }

        for (Material m : materiaisVencendo) {
            System.out.println("⚠ Alerta: Material próximo do vencimento → " + m.getNome());
        }
    }


}