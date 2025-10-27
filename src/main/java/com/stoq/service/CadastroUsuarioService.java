package com.stoq.service;

import com.stoq.domain.Funcionario;
import com.stoq.dto.FuncionarioDTO.*;
import com.stoq.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço responsável por gerar, validar e exibir QR Code
 */
@Service
public class CadastroUsuarioService {

    private final FuncionarioRepository repository;

    public CadastroUsuarioService(FuncionarioRepository repository) {
        this.repository = repository;
    }

    // ========= MÉTODOS DE ESCRITA =========

    /**
     * Cadastra um novo funcionário.
     */
    @Transactional
    public FuncionarioResponseDTO cadastrar(Long solicitanteId, FuncionarioRequestDTO request) {
        Funcionario solicitante = obterObrigatorio(solicitanteId);
        exigirAdminOuGestor(solicitante);

        // Validação de unicidade no Service (otimizada)
        exigirEmailUnico(request.email(), null);

        Funcionario novo = toEntity(request);
        novo.setDtCadastro(LocalDate.now());

        // Garante que o campo ATIVO seja "S" se não foi preenchido
        if (novo.getAtivo() == null || novo.getAtivo().isBlank()) {
            novo.setAtivo("S");
        }

        Funcionario salvo = repository.save(novo);
        return toResponseDTO(salvo);
    }

    /**
     * Atualiza dados de um funcionário.
     */
    @Transactional
    public FuncionarioResponseDTO atualizar(Long solicitanteId, Long idFuncionario, FuncionarioRequestDTO dados) {
        Funcionario solicitante = obterObrigatorio(solicitanteId);
        exigirAdminOuGestor(solicitante);

        Funcionario atual = obterObrigatorio(idFuncionario);

        // Checagem de e-mail único (se alterado)
        if (dados.email() != null && !dados.email().isBlank() && !Objects.equals(atual.getEmail(), dados.email())) {
            exigirEmailUnico(dados.email(), idFuncionario);
            atual.setEmail(dados.email());
        }

        // Mapeia e atualiza os campos (o DTO já garante que os campos são válidos)
        atual.setNome(dados.nome());
        atual.setCpf(dados.cpf());
        atual.setCargo(dados.cargo());
        atual.setIdLaboratorio(dados.idLaboratorio());

        // Atualiza 'ativo' se o DTO tiver o campo
        if (dados.ativo() != null) {
            atual.setAtivo(dados.ativo());
        }

        Funcionario salvo = repository.save(atual);
        return toResponseDTO(salvo);
    }

    @Transactional
    public void ativar(Long solicitanteId, Long idFuncionario) {
        Funcionario solicitante = obterObrigatorio(solicitanteId);
        exigirAdminOuGestor(solicitante);
        Funcionario f = obterObrigatorio(idFuncionario);
        f.setAtivo("S");
        repository.save(f);
    }

    @Transactional
    public void desativar(Long solicitanteId, Long idFuncionario) {
        Funcionario solicitante = obterObrigatorio(solicitanteId);
        exigirAdminOuGestor(solicitante);
        Funcionario f = obterObrigatorio(idFuncionario);
        f.setAtivo("N");
        repository.save(f);
    }

    /**
     * Exclusão definitiva (DELETE). Restrita a ADMIN.
     */
    @Transactional
    public void deletar(Long solicitanteId, Long idFuncionario) {
        Funcionario solicitante = obterObrigatorio(solicitanteId);
        exigirAdmin(solicitante);

        if (!repository.existsById(idFuncionario)) {
            throw new NoSuchElementException("Funcionário não encontrado: " + idFuncionario);
        }
        repository.deleteById(idFuncionario);
    }

    // ========= CONSULTAS (READ) =========

    @Transactional(readOnly = true)
    public FuncionarioResponseDTO buscarPorId(Long id) {
        return toResponseDTO(obterObrigatorio(id));
    }

    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> listarTodos() {
        return repository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> listarPorLaboratorio(Long idLaboratorio) {
        return repository.findByIdLaboratorio(idLaboratorio).stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> listarAtivosPorCargo(String cargo) {
        return repository.findByAtivoAndCargoIgnoreCase("S", cargo).stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    // ========= VALIDAÇÕES & MAPPER =========

    private Funcionario obterObrigatorio(Long id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Funcionário não encontrado: " + id));
    }

    private void exigirAdmin(Funcionario f) {
        if (!"ADMIN".equalsIgnoreCase(f.getCargo()))
            throw new SecurityException("Ação restrita a ADMIN.");
    }

    private void exigirAdminOuGestor(Funcionario f) {
        if (!( "ADMIN".equalsIgnoreCase(f.getCargo()) || "GESTOR".equalsIgnoreCase(f.getCargo()) ))
            throw new SecurityException("Ação restrita a ADMIN ou GESTOR.");
    }

    private void exigirEmailUnico(String email, Long ignorarIdFuncionario) {
        Optional<Funcionario> existing = repository.findByEmailIgnoreCase(email);

        if (existing.isPresent() && !Objects.equals(existing.get().getIdFuncionario(), ignorarIdFuncionario)) {
            throw new IllegalArgumentException("E-mail já cadastrado: " + email);
        }
    }

    // Mapeamento de DTO de Requisição para Entidade
    private Funcionario toEntity(FuncionarioRequestDTO dto) {
        Funcionario f = new Funcionario();
        f.setNome(dto.nome());
        f.setCpf(dto.cpf());
        f.setEmail(dto.email());
        f.setCargo(dto.cargo());
        f.setIdLaboratorio(dto.idLaboratorio());
        f.setAtivo(dto.ativo());
        return f;
    }

    // Mapeamento de Entidade para DTO de Resposta
    private FuncionarioResponseDTO toResponseDTO(Funcionario f) {
        return new FuncionarioResponseDTO(
                f.getIdFuncionario(),
                f.getNome(),
                f.getCpf(),
                f.getEmail(),
                f.getCargo(),
                f.getIdLaboratorio(),
                f.getAtivo(),
                f.getDtCadastro()
        );
    }
}