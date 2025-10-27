package com.stoq.controller;

import com.stoq.dto.EstoqueDTO.EstoqueResponseDTO;
import com.stoq.service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation; // Adicionado
import io.swagger.v3.oas.annotations.tags.Tag; // Adicionado
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/estoque")
@Tag(name = "Estoque", description = "Gestão de Saldo, Entrada/Saída e Histórico de Materiais") // Adicionado
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    /**
     * Endpoint para dar entrada de materiais no estoque após o recebimento de um pedido.
     */
    @Operation(summary = "Registra entrada de estoque consolidada por pedido e atualiza status do pedido") // Adicionado
    @PutMapping("/entrada/{pedidoId}/{recebedorId}")
    public ResponseEntity<String> entradaEstoquePorPedido(
            @PathVariable Long pedidoId,
            @PathVariable Long recebedorId) {
        try {
            estoqueService.entradaEstoquePorPedido(pedidoId, recebedorId);
            return ResponseEntity.ok("Entrada de estoque e atualização do Pedido " + pedidoId + " concluídas com sucesso.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno na entrada de estoque: " + e.getMessage());
        }
    }

    /**
     * Endpoint para listar todo o estoque atual (último saldo diário) de um laboratório.
     */
    @Operation(summary = "Lista o saldo atual consolidado do estoque por laboratório") // Adicionado
    @GetMapping("/laboratorio/{laboratorioId}")
    public ResponseEntity<List<EstoqueResponseDTO>> listarEstoquePorLaboratorio(@PathVariable Long laboratorioId) {
        // O Service deve ser ajustado para mapear para DTOs
        List<EstoqueResponseDTO> estoque = estoqueService.listarEstoquePorLaboratorio(laboratorioId);
        return ResponseEntity.ok(estoque);
    }

    /**
     * Endpoint para acionar manualmente o job de consolidação do histórico.
     */
    @Operation(summary = "Aciona manualmente o job de consolidação do Histórico de Estoque diário") // Adicionado
    @PostMapping("/consolidar")
    public ResponseEntity<String> consolidarHistorico() {
        try {
            estoqueService.consolidarHistoricoDiario();
            return ResponseEntity.ok("Consolidação do histórico de estoque diário iniciada com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao consolidar histórico: " + e.getMessage());
        }
    }
}