package com.stoq.service;

import com.stoq.dto.AnalyticsDTO.*;
import com.stoq.repository.MovimentacaoEstoqueRepository;
import com.stoq.repository.PedidoRepository;
import com.stoq.repository.PedidoItensRepository;
import com.stoq.repository.MaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final MovimentacaoEstoqueRepository movRepository;
    private final PedidoRepository pedidoRepository;
    private final PedidoItensRepository pedidoItensRepository;
    private final MaterialRepository materialRepository;

    public AnalyticsService(
            MovimentacaoEstoqueRepository movRepository,
            PedidoRepository pedidoRepository,
            PedidoItensRepository pedidoItensRepository,
            MaterialRepository materialRepository) {
        this.movRepository = movRepository;
        this.pedidoRepository = pedidoRepository;
        this.pedidoItensRepository = pedidoItensRepository;
        this.materialRepository = materialRepository;
    }

    /**
     * Retirada e reposição ao longo do tempo (Agregado Diário)
     */
    @Transactional(readOnly = true)
    public Map<LocalDate, Map<String, BigDecimal>> getMovimentacoesAoLongoDoTempo() {
        List<Object[]> results = movRepository.getMovimentacoesAoLongoDoTempo();
        Map<LocalDate, Map<String, BigDecimal>> resultado = new HashMap<>();

        for (Object[] row : results) {
            LocalDate dia = ((java.sql.Date) row[0]).toLocalDate();
            String tipo = (String) row[1];
            BigDecimal total = new BigDecimal(row[2].toString());

            resultado.putIfAbsent(dia, new HashMap<>());
            resultado.get(dia).put(tipo, total);
        }
        return resultado;
    }

    /**
     * Frequência de retirada por enfermeiro (Mapa: Funcionario ID -> Total de Retiradas)
     */
    @Transactional(readOnly = true)
    public Map<Long, Integer> getFrequenciaPorEnfermeiro() {
        List<Object[]> results = movRepository.getFrequenciaPorEnfermeiro();
        return results.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> ((Number) row[1]).intValue()
                ));
    }

    /**
     * Materiais mais utilizados na semana
     */
    @Transactional(readOnly = true)
    public List<UsoMaterialDTO> getMateriaisMaisUsadosNaSemana() {
        List<Object[]> results = movRepository.getMateriaisMaisUsadosNaSemana();
        return results.stream()
                .map(row -> {
                    UsoMaterialDTO dto = new UsoMaterialDTO();
                    dto.setIdMaterial(((Number) row[0]).longValue());
                    dto.setTotalUso(new BigDecimal(row[1].toString()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Dias/horários de maior movimento
     */
    @Transactional(readOnly = true)
    public Map<String, Integer> getMovimentoPorDiaDaSemana() {
        List<Object[]> results = movRepository.getMovimentoPorDiaDaSemana();
        return results.stream()
                .collect(Collectors.toMap(
                        row -> ((String) row[0]).trim(),
                        row -> ((Number) row[1]).intValue()
                ));
    }

    /**
     * Taxa de materiais vencidos descartados por mês
     */
    @Transactional(readOnly = true)
    public Map<String, Integer> getMateriaisVencidosPorMes() {
        List<Object[]> results = pedidoItensRepository.getMateriaisVencidosPorMes();
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> ((Number) row[1]).intValue()
                ));
    }

    /**
     * Tempo médio entre pedido e recebimento
     */
    @Transactional(readOnly = true)
    public Double getTempoMedioEntregaPedidos() {
        return pedidoRepository.getTempoMedioEntregaPedidos();
    }

    /**
     * Comparação entre estoque mínimo e uso real
     */
    @Transactional(readOnly = true)
    public List<EstoqueUsoComparacaoDTO> getComparacaoEstoqueMinimoUso() {
        List<Object[]> results = materialRepository.getComparacaoEstoqueMinimoUso();
        return results.stream()
                .map(row -> {
                    BigDecimal estoqueMinimo = new BigDecimal(row[1].toString());
                    BigDecimal usoReal = new BigDecimal(row[2].toString());

                    EstoqueUsoComparacaoDTO dto = new EstoqueUsoComparacaoDTO();
                    dto.setIdMaterial(((Number) row[0]).longValue());
                    dto.setEstoqueMinimo(estoqueMinimo);
                    dto.setUsoReal(usoReal);
                    dto.setDiferenca(usoReal.subtract(estoqueMinimo));
                    return dto;
                })
                .collect(Collectors.toList());
    }
}