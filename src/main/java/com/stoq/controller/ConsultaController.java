package com.stoq.controller;

import com.stoq.dto.ConsultaDTO.ConsultaRequestDTO;
import com.stoq.dto.ConsultaDTO.ConsultaResponseDTO;
import com.stoq.service.ConsultaService;
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
@RequestMapping("/api/consultas")
@Tag(name = "Consultas", description = "Gestão de agendamentos e registros de consultas")
public class ConsultaController {

    private final ConsultaService service;

    public ConsultaController(ConsultaService service) {
        this.service = service;
    }

    // LISTAR TUDO
    @Operation(summary = "Listar todas as consultas cadastradas")
    @GetMapping
    public ResponseEntity<List<ConsultaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // BUSCAR POR ID
    @Operation(summary = "Buscar uma consulta por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> buscarPorId(@PathVariable Long id) {
        try {
            ConsultaResponseDTO response = service.buscarPorId(id);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // CADASTRAR
    @Operation(summary = "Cadastrar uma nova consulta")
    @PostMapping
    public ResponseEntity<ConsultaResponseDTO> cadastrar(@Valid @RequestBody ConsultaRequestDTO request, UriComponentsBuilder uri) {
        ConsultaResponseDTO response = service.cadastrar(request);
        return ResponseEntity.created(uri.path("/api/consultas/{id}")
                        .buildAndExpand(response.idConsulta())
                        .toUri())
                .body(response);
    }

    // EDITAR
    @Operation(summary = "Editar as informações de uma consulta existente")
    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> editar(@PathVariable Long id, @Valid @RequestBody ConsultaRequestDTO request) {
        try {
            ConsultaResponseDTO response = service.editar(id, request);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // DELETAR
    @Operation(summary = "Deletar uma consulta")
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