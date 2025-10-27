package com.stoq.controller;

import com.stoq.dto.AnalyticsDTO.*;
import com.stoq.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "Consultas e Agregações para Dashboards e Métricas")
public class AnalyticsController {

    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    @Operation(summary = "Retirada e reposição de materiais ao longo do tempo (Gráfico)")
    @GetMapping("/movimentacao-tempo")
    public ResponseEntity<Map<LocalDate, Map<String, BigDecimal>>> getMovimentacoesAoLongoDoTempo() {
        return ResponseEntity.ok(service.getMovimentacoesAoLongoDoTempo());
    }

    @Operation(summary = "Frequência total de retiradas por enfermeiro")
    @GetMapping("/frequencia-enfermeiro")
    public ResponseEntity<Map<Long, Integer>> getFrequenciaPorEnfermeiro() {
        return ResponseEntity.ok(service.getFrequenciaPorEnfermeiro());
    }

    @Operation(summary = "Materiais mais utilizados na última semana")
    @GetMapping("/materiais-mais-usados")
    public ResponseEntity<List<UsoMaterialDTO>> getMateriaisMaisUsadosNaSemana() {
        return ResponseEntity.ok(service.getMateriaisMaisUsadosNaSemana());
    }

    @Operation(summary = "Volume de movimento por dia da semana (Peak Times)")
    @GetMapping("/movimento-dia-semana")
    public ResponseEntity<Map<String, Integer>> getMovimentoPorDiaDaSemana() {
        return ResponseEntity.ok(service.getMovimentoPorDiaDaSemana());
    }

    @Operation(summary = "Contagem de materiais vencidos descartados por mês/ano")
    @GetMapping("/materiais-vencidos-mes")
    public ResponseEntity<Map<String, Integer>> getMateriaisVencidosPorMes() {
        return ResponseEntity.ok(service.getMateriaisVencidosPorMes());
    }

    @Operation(summary = "Tempo médio de entrega de pedidos (dias)")
    @GetMapping("/tempo-medio-entrega")
    public ResponseEntity<Double> getTempoMedioEntregaPedidos() {
        return ResponseEntity.ok(service.getTempoMedioEntregaPedidos());
    }

    @Operation(summary = "Comparação entre estoque mínimo e uso real (Superavit/Deficit)")
    @GetMapping("/comparacao-estoque-uso")
    public ResponseEntity<List<EstoqueUsoComparacaoDTO>> getComparacaoEstoqueMinimoUso() {
        return ResponseEntity.ok(service.getComparacaoEstoqueMinimoUso());
    }
}