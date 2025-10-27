package com.stoq.controller;

import com.stoq.service.FechamentoEstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/fechamento-estoque")
@Tag(name = "Fechamento de Estoque", description = "Rotinas diárias para consolidação de Histórico e Limpeza de Saldo.")
public class FechamentoEstoqueController {

    private final FechamentoEstoqueService service;

    public FechamentoEstoqueController(FechamentoEstoqueService service) {
        this.service = service;
    }

    /**
     * Dispara o fechamento do estoque para a data atual.
     * URL: POST /api/fechamento-estoque/hoje
     */
    @Operation(summary = "Aciona o fechamento do estoque para a data de hoje (Copia para Histórico e Limpa Saldo Antigo)")
    @PostMapping("/hoje")
    public ResponseEntity<String> fecharDiaAtual() {
        try {
            LocalDate hoje = LocalDate.now();
            service.fecharDia(hoje);
            return ResponseEntity.ok("Fechamento de estoque concluído com sucesso para o dia: " + hoje);
        } catch (Exception e) {
            // Em caso de falha, o @Transactional reverte as ações
            return ResponseEntity.internalServerError().body("Falha na rotina de fechamento de estoque: " + e.getMessage());
        }
    }

    /**
     * Dispara o fechamento do estoque para uma data específica (Útil para testes).
     * URL: POST /api/fechamento-estoque/{data}
     */
    @Operation(summary = "Aciona o fechamento do estoque para uma data específica (Formato YYYY-MM-DD)")
    @PostMapping("/{data}")
    public ResponseEntity<String> fecharDiaEspecifico(@PathVariable LocalDate data) {
        try {
            service.fecharDia(data);
            return ResponseEntity.ok("Fechamento de estoque concluído com sucesso para o dia: " + data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Falha na rotina de fechamento de estoque para " + data + ": " + e.getMessage());
        }
    }
}