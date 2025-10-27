package com.stoq.controller;

import com.stoq.dto.MaterialDTO.MaterialRequestDTO;
import com.stoq.dto.MaterialDTO.MaterialResponseDTO;
import com.stoq.service.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/materiais")
@Tag(name = "Material", description = "CRUD das informações dos Materiais e Insumos")
public class MaterialController {

    private final MaterialService service;

    public MaterialController(MaterialService service) {
        this.service = service;
    }

    // LISTAR TUDO
    @Operation(summary = "Listar todos os materiais cadastrados")
    @GetMapping
    public ResponseEntity<List<MaterialResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // LISTAR ATIVOS
    @Operation(summary = "Listar apenas materiais ativos")
    @GetMapping("/ativos")
    public ResponseEntity<List<MaterialResponseDTO>> listarAtivos() {
        return ResponseEntity.ok(service.listarAtivos());
    }

    // BUSCAR POR ID
    @Operation(summary = "Buscar um material por ID")
    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponseDTO> buscarPorId(@PathVariable Long id) {
        try {
            MaterialResponseDTO response = service.buscarPorId(id);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // CADASTRAR
    @Operation(summary = "Cadastrar um novo material")
    @PostMapping
    public ResponseEntity<MaterialResponseDTO> cadastrar(@Valid @RequestBody MaterialRequestDTO request, UriComponentsBuilder uri) {
        MaterialResponseDTO response = service.cadastrar(request);
        return ResponseEntity.created(uri.path("/api/materiais/{id}")
                        .buildAndExpand(response.idMaterial()) // Usando o método de acesso do record
                        .toUri())
                .body(response);
    }

    // EDITAR
    @Operation(summary = "Editar as informações de um material existente")
    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponseDTO> editar(@PathVariable Long id, @Valid @RequestBody MaterialRequestDTO request) {
        try {
            MaterialResponseDTO response = service.editar(id, request);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // DELETAR
    @Operation(summary = "Deletar um material (exclusão lógica ou física)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}