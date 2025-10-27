package com.stoq.controller;

import com.stoq.dto.PresetDTO.PresetItemRequestDTO;
import com.stoq.dto.PresetDTO.PresetRequestDTO;
import com.stoq.dto.PresetDTO.PresetResponseDTO;
import com.stoq.service.CadastroPresetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/presets")
@Tag(name = "Presets", description = "Gestão de Presets de Materiais para Consultas (Restrito a ADMIN/GESTOR)")
public class PresetController {

    private final CadastroPresetService service;

    public PresetController(CadastroPresetService service) {
        this.service = service;
    }

    // LISTAR TUDO
    @Operation(summary = "Lista todos os presets, incluindo itens")
    @GetMapping
    public ResponseEntity<List<PresetResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // LISTAR ATIVOS
    @Operation(summary = "Lista apenas presets ativos")
    @GetMapping("/ativos")
    public ResponseEntity<List<PresetResponseDTO>> listarAtivos() {
        return ResponseEntity.ok(service.listarAtivos());
    }

    // BUSCAR POR ID
    @Operation(summary = "Busca um preset completo por ID")
    @GetMapping("/{idPreset}")
    public ResponseEntity<PresetResponseDTO> buscarPorId(@PathVariable Long idPreset) {
        try {
            return ResponseEntity.ok(service.buscarPorId(idPreset));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // CADASTRAR
    @Operation(summary = "Cadastra um novo preset e seus materiais (Restrito a ADMIN/GESTOR)")
    @PostMapping("/{solicitanteId}")
    public ResponseEntity<PresetResponseDTO> criar(
            @PathVariable Long solicitanteId,
            @Valid @RequestBody PresetRequestDTO request,
            @RequestParam(required = false) List<PresetItemRequestDTO> itens,
            UriComponentsBuilder uri) {
        try {
            PresetResponseDTO response = service.criar(solicitanteId, request, itens);
            return ResponseEntity.created(uri.path("/api/presets/{id}")
                            .buildAndExpand(response.idPreset())
                            .toUri())
                    .body(response);
        } catch (SecurityException | IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    // ATUALIZAR
    @Operation(summary = "Atualiza nome/código/descrição do preset (Restrito a ADMIN/GESTOR)")
    @PutMapping("/{idPreset}/{solicitanteId}")
    public ResponseEntity<PresetResponseDTO> atualizar(
            @PathVariable Long idPreset,
            @PathVariable Long solicitanteId,
            @Valid @RequestBody PresetRequestDTO dados) {
        try {
            PresetResponseDTO response = service.atualizar(solicitanteId, idPreset, dados);
            return ResponseEntity.ok(response);
        } catch (SecurityException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // DEFINIR ITENS
    @Operation(summary = "Substitui TODOS os itens de um preset por uma nova lista (Restrito a ADMIN/GESTOR)")
    @PutMapping("/{idPreset}/itens/{solicitanteId}")
    public ResponseEntity<PresetResponseDTO> definirItens(
            @PathVariable Long idPreset,
            @PathVariable Long solicitanteId,
            @Valid @RequestBody List<PresetItemRequestDTO> itens) {
        try {
            PresetResponseDTO response = service.definirItens(solicitanteId, idPreset, itens);
            return ResponseEntity.ok(response);
        } catch (SecurityException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ADICIONAR/ATUALIZAR ITEM INDIVIDUAL
    @Operation(summary = "Adiciona ou atualiza um item individual no Preset (Restrito a ADMIN/GESTOR)")
    @PostMapping("/{idPreset}/itens/{solicitanteId}/item")
    public ResponseEntity<Void> adicionarOuAtualizarItem(
            @PathVariable Long idPreset,
            @PathVariable Long solicitanteId,
            @Valid @RequestBody PresetItemRequestDTO item) {
        try {
            service.adicionarOuAtualizarItem(solicitanteId, idPreset, item.idMaterial(), item.qtdePorExame());
            return ResponseEntity.ok().build();
        } catch (SecurityException | IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // REMOVER ITEM INDIVIDUAL
    @Operation(summary = "Remove um item individual do Preset (Restrito a ADMIN/GESTOR)")
    @DeleteMapping("/{idPreset}/itens/{solicitanteId}/item/{idMaterial}")
    public ResponseEntity<Void> removerItem(
            @PathVariable Long idPreset,
            @PathVariable Long solicitanteId,
            @PathVariable Long idMaterial) {
        try {
            service.removerItem(solicitanteId, idPreset, idMaterial);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // DELETAR
    @Operation(summary = "Deleta um preset e todos os seus itens (Restrito a ADMIN/GESTOR)")
    @DeleteMapping("/{idPreset}/{solicitanteId}")
    public ResponseEntity<Void> deletar(@PathVariable Long idPreset, @PathVariable Long solicitanteId) {
        try {
            service.deletar(solicitanteId, idPreset);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}