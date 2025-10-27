package com.stoq.controller;

import com.stoq.domain.Laboratorio;
import com.stoq.dto.LaboratorioDTO.CadastrarLaboratorioRequestDTO;
import com.stoq.dto.LaboratorioDTO.CadastrarLaboratorioResponseDTO;
import com.stoq.service.LaboratorioService;
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
@RequestMapping("/api/laboratorio")
@Tag(name = "Laboratorio", description = "CRUD das informações dos Laboratórios")
public class LaboratorioController {

    private final LaboratorioService service;
    public LaboratorioController(LaboratorioService service) {this.service = service;}

    // LISTAR TUDO
    @Operation(summary = "Listar todos os laboratórios cadastrados")
    @GetMapping
    public ResponseEntity<List<CadastrarLaboratorioResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // LISTAR LABORATORIOS ATIVOS
    @Operation(summary = "Listar todos os laboratórios ativos")
    @GetMapping("/ativos")
    public ResponseEntity<List<CadastrarLaboratorioResponseDTO>> ativos() {
        return ResponseEntity.ok(service.listarAtivos());
    }

    @Operation(summary = "Buscar laboratório por ID")
    @GetMapping("/{id}")
    public ResponseEntity<CadastrarLaboratorioResponseDTO> buscarPorId(@PathVariable Long id) {
        try {
            CadastrarLaboratorioResponseDTO response = service.buscarPorId(id);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // CADASTRAR
    @Operation(summary = "Cadastrar um novo laboratório")
    @PostMapping
    public ResponseEntity<CadastrarLaboratorioResponseDTO> cadastrar(@Valid @RequestBody CadastrarLaboratorioRequestDTO request, UriComponentsBuilder uri) {
        CadastrarLaboratorioResponseDTO response = service.cadastrar(request);
        return ResponseEntity.created(uri.path("/api/laboratorio/{id}") // URL de retorno ajustada
                        .buildAndExpand(response.idLab())
                        .toUri())
                .body(response);
    }

    // EDITAR
    @Operation(summary = "Editar as informações de um laboratório existente")
    @PutMapping("/{id}")
    public ResponseEntity<CadastrarLaboratorioResponseDTO> editar(@PathVariable Long id, @Valid @RequestBody CadastrarLaboratorioRequestDTO request) {
        try {
            CadastrarLaboratorioResponseDTO response = service.editar(id, request);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // DELETAR
    @Operation(summary = "Deletar um laboratório")
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