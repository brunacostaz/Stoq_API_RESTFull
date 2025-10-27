package com.stoq.controller;

import com.stoq.dto.FuncionarioDTO.FuncionarioRequestDTO;
import com.stoq.dto.FuncionarioDTO.FuncionarioResponseDTO;
import com.stoq.service.CadastroUsuarioService;
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
@RequestMapping("/api/funcionarios")
@Tag(name = "Funcionários", description = "Cadastro e Gestão de Usuários (Admin, Gestor, Enfermeiro)")
public class FuncionarioController {

    private final CadastroUsuarioService service;

    public FuncionarioController(CadastroUsuarioService service) {
        this.service = service;
    }

    // Assumimos que o ID do solicitante logado virá de algum lugar (Header, Token, Query Param)
    // Usaremos @RequestParam Long solicitanteId para fins de teste no Insomnia/Swagger

    // LISTAR TUDO
    @Operation(summary = "Lista todos os funcionários cadastrados")
    @GetMapping
    public ResponseEntity<List<FuncionarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // BUSCAR POR ID
    @Operation(summary = "Busca um funcionário por ID")
    @GetMapping("/{idFuncionario}")
    public ResponseEntity<FuncionarioResponseDTO> buscarPorId(@PathVariable Long idFuncionario) {
        try {
            return ResponseEntity.ok(service.buscarPorId(idFuncionario));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // CADASTRAR
    @Operation(summary = "Cadastra um novo funcionário (Restrito a ADMIN/GESTOR)")
    @PostMapping
    public ResponseEntity<FuncionarioResponseDTO> cadastrar(
            @RequestParam Long solicitanteId,
            @Valid @RequestBody FuncionarioRequestDTO request,
            UriComponentsBuilder uri) {
        try {
            FuncionarioResponseDTO response = service.cadastrar(solicitanteId, request);
            return ResponseEntity.created(uri.path("/api/funcionarios/{id}")
                            .buildAndExpand(response.idFuncionario())
                            .toUri())
                    .body(response);
        } catch (SecurityException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    // ATUALIZAR
    @Operation(summary = "Atualiza dados de um funcionário (Restrito a ADMIN/GESTOR)")
    @PutMapping("/{idFuncionario}")
    public ResponseEntity<FuncionarioResponseDTO> atualizar(
            @RequestParam Long solicitanteId,
            @PathVariable Long idFuncionario,
            @Valid @RequestBody FuncionarioRequestDTO request) {
        try {
            FuncionarioResponseDTO response = service.atualizar(solicitanteId, idFuncionario, request);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // DELETAR (Restrito a ADMIN)
    @Operation(summary = "Deleta um funcionário (Exclusão Definitiva, Restrito a ADMIN)")
    @DeleteMapping("/{idFuncionario}")
    public ResponseEntity<Void> deletar(@RequestParam Long solicitanteId, @PathVariable Long idFuncionario) {
        try {
            service.deletar(solicitanteId, idFuncionario);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ATIVAR/DESATIVAR (Ações de Status)
    @Operation(summary = "Ativa um funcionário ('S', Restrito a ADMIN/GESTOR)")
    @PutMapping("/{idFuncionario}/ativar")
    public ResponseEntity<Void> ativar(@RequestParam Long solicitanteId, @PathVariable Long idFuncionario) {
        try {
            service.ativar(solicitanteId, idFuncionario);
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Desativa um funcionário ('N', Restrito a ADMIN/GESTOR)")
    @PutMapping("/{idFuncionario}/desativar")
    public ResponseEntity<Void> desativar(@RequestParam Long solicitanteId, @PathVariable Long idFuncionario) {
        try {
            service.desativar(solicitanteId, idFuncionario);
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}