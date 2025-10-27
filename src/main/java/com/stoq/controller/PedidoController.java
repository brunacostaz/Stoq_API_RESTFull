package com.stoq.controller;

import com.stoq.dto.PedidoDTO.PedidoCriacaoRequestDTO;
import com.stoq.dto.PedidoDTO.PedidoRecebimentoRequestDTO;
import com.stoq.dto.PedidoDTO.PedidoResponseDTO;
import com.stoq.service.PedidoService;
import com.stoq.service.FuncionarioService;
import io.swagger.v3.oas.annotations.Operation; // Adicionado
import io.swagger.v3.oas.annotations.tags.Tag; // Adicionado
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Gestão de Pedidos de Compra e Recebimento de Materiais") // Adicionado
public class PedidoController {

    private final PedidoService pedidoService;
    private final FuncionarioService funcionarioService;

    public PedidoController(PedidoService pedidoService, FuncionarioService funcionarioService) {
        this.pedidoService = pedidoService;
        this.funcionarioService = funcionarioService;
    }

    // --- Endpoints de Ação ---

    @Operation(summary = "Cria um novo pedido PENDENTE, sugerindo itens de reposição") // Adicionado
    @PostMapping("/criar")
    public ResponseEntity<?> criarPedido(@RequestBody PedidoCriacaoRequestDTO solicitacao) {
        try {
            Long idPedido = pedidoService.realizarPedido(
                    solicitacao.getMateriaisEmBaixa(),
                    solicitacao.getSolicitanteId(),
                    solicitacao.getIdLaboratorio()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body("Pedido gerado com sucesso! ID: " + idPedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao criar pedido: " + e.getMessage());
        }
    }

    // ... (enviarPedido e cancelarPedido)

    @Operation(summary = "Recebe o pedido, atualiza os itens com lote/validade e aciona a entrada no estoque") // Adicionado
    @PutMapping("/{idPedido}/receber")
    public ResponseEntity<String> receberPedido(
            @PathVariable Long idPedido,
            @RequestParam Long recebedorId,
            // O corpo recebe um mapa onde a chave é o ID do material (Long) e o valor é o DTO de recebimento
            @RequestBody Map<Long, PedidoRecebimentoRequestDTO> recebimentos) {
        try {
            pedidoService.receberPedido(idPedido, recebedorId, recebimentos);
            return ResponseEntity.ok("Pedido recebido com sucesso! Estoque atualizado.");
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno no recebimento: " + e.getMessage());
        }
    }

    // --- Endpoints de Leitura (Usando Response DTOs) ---

    @Operation(summary = "Busca um pedido completo por ID") // Adicionado
    @GetMapping("/{idPedido}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long idPedido) {
        try {
            PedidoResponseDTO response = pedidoService.buscarPorId(idPedido);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Lista todos os pedidos (com paginação no futuro)") // Adicionado
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        List<PedidoResponseDTO> pedidos = pedidoService.listarTodos();
        return ResponseEntity.ok(pedidos);
    }
}