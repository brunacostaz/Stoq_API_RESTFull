package com.stoq.service;

import com.stoq.domain.Funcionario;
import com.stoq.domain.Material;
import com.stoq.domain.Preset;
import com.stoq.domain.PresetMaterial;
import com.stoq.domain.PresetMaterialID;
import com.stoq.dto.PresetDTO.PresetItemRequestDTO;
import com.stoq.dto.PresetDTO.PresetItemResponseDTO;
import com.stoq.dto.PresetDTO.PresetRequestDTO;
import com.stoq.dto.PresetDTO.PresetResponseDTO;
import com.stoq.repository.MaterialRepository;
import com.stoq.repository.PresetRepository;
import com.stoq.repository.PresetMaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Cadastro e manutenção de Presets e seus materiais (preset_materiais).
 * Permissões: ADMIN ou GESTOR.
 */
@Service
public class CadastroPresetService {

    private final PresetRepository presetRepository;
    private final PresetMaterialRepository presetMaterialRepository;
    private final MaterialRepository materialRepository;
    private final FuncionarioService funcionarioService;

    public CadastroPresetService(PresetRepository presetRepository,
                                 PresetMaterialRepository presetMaterialRepository,
                                 MaterialRepository materialRepository,
                                 FuncionarioService funcionarioService) {
        this.presetRepository = presetRepository;
        this.presetMaterialRepository = presetMaterialRepository;
        this.materialRepository = materialRepository;
        this.funcionarioService = funcionarioService;
    }

    // ========== AÇÕES DE PRESET ==========

    /**
     * Cria o cabeçalho do Preset e, opcionalmente, define seus itens.
     */
    @Transactional
    public PresetResponseDTO criar(Long solicitanteId, PresetRequestDTO request, List<PresetItemRequestDTO> itens) {
        Funcionario solicitante = funcionarioService.buscarPorId(solicitanteId);
        exigirAdminOuGestor(solicitante);
        exigirCodigoUnico(request.codigo(), null);

        Preset preset = new Preset();
        preset.setNome(request.nome());
        preset.setCodigo(request.codigo());
        preset.setDescricao(request.descricao());
        preset.setAtivo(request.ativo() == null || request.ativo().isBlank() ? "S" : request.ativo());

        preset = presetRepository.save(preset);
        Long idPreset = preset.getIdPreset();

        if (itens != null && !itens.isEmpty()) {
            definirItensInterno(idPreset, itens);
        }

        return toResponseDTO(preset, presetMaterialRepository.findByIdIdPreset(idPreset));
    }

    /**
     * Atualiza as informações básicas do Preset (nome, código, descrição, ativo).
     */
    @Transactional
    public PresetResponseDTO atualizar(Long solicitanteId, Long idPreset, PresetRequestDTO dados) {
        Funcionario solicitante = funcionarioService.buscarPorId(solicitanteId);
        exigirAdminOuGestor(solicitante);
        Preset atual = obterPresetObrigatorio(idPreset);

        if (!Objects.equals(atual.getCodigo(), dados.codigo())) {
            exigirCodigoUnico(dados.codigo(), idPreset);
            atual.setCodigo(dados.codigo());
        }

        atual.setNome(dados.nome());
        atual.setDescricao(dados.descricao());
        if (dados.ativo() != null) atual.setAtivo(dados.ativo());

        Preset atualizado = presetRepository.save(atual);
        return toResponseDTO(atualizado, presetMaterialRepository.findByIdIdPreset(idPreset));
    }

    /**
     * Ativa o Preset (ativo = 'S').
     */
    @Transactional
    public void ativar(Long solicitanteId, Long idPreset) {
        Funcionario solicitante = funcionarioService.buscarPorId(solicitanteId);
        exigirAdminOuGestor(solicitante);
        Preset p = obterPresetObrigatorio(idPreset);
        p.setAtivo("S");
        presetRepository.save(p);
    }

    /**
     * Desativa o Preset (ativo = 'N').
     */
    @Transactional
    public void desativar(Long solicitanteId, Long idPreset) {
        Funcionario solicitante = funcionarioService.buscarPorId(solicitanteId);
        exigirAdminOuGestor(solicitante);
        Preset p = obterPresetObrigatorio(idPreset);
        p.setAtivo("N");
        presetRepository.save(p);
    }

    /**
     * Deleta o Preset e todos os seus itens associados.
     */
    @Transactional
    public void deletar(Long solicitanteId, Long idPreset) {
        Funcionario solicitante = funcionarioService.buscarPorId(solicitanteId);
        exigirAdminOuGestor(solicitante);
        obterPresetObrigatorio(idPreset);

        removerTodosItens(idPreset);
        presetRepository.deleteById(idPreset);
    }

    // ========== AÇÕES DE ITENS ==========

    /**
     * Substitui todos os itens de um preset pela nova lista.
     */
    @Transactional
    public PresetResponseDTO definirItens(Long solicitanteId, Long idPreset, List<PresetItemRequestDTO> itens) {
        Funcionario solicitante = funcionarioService.buscarPorId(solicitanteId);
        exigirAdminOuGestor(solicitante);
        Preset preset = obterPresetObrigatorio(idPreset);

        if (itens == null || itens.isEmpty()) {
            removerTodosItens(idPreset);
            return toResponseDTO(preset, Collections.emptyList());
        }

        definirItensInterno(idPreset, itens);

        return toResponseDTO(preset, presetMaterialRepository.findByIdIdPreset(idPreset));
    }

    /**
     * Adiciona ou atualiza um item individual no Preset.
     */
    @Transactional
    public void adicionarOuAtualizarItem(Long solicitanteId, Long idPreset, Long idMaterial, BigDecimal qtdePorExame) {
        Funcionario solicitante = funcionarioService.buscarPorId(solicitanteId);
        exigirAdminOuGestor(solicitante);
        obterPresetObrigatorio(idPreset);
        validarMaterialExiste(idMaterial);
        validarQtdePorExame(qtdePorExame);

        PresetMaterialID id = new PresetMaterialID(idPreset, idMaterial);

        // O save do JPA faz o INSERT se não existir e o UPDATE se existir (upsert)
        PresetMaterial pm = new PresetMaterial(id, qtdePorExame);
        presetMaterialRepository.save(pm);
    }

    /**
     * Remove um item individual do Preset.
     */
    @Transactional
    public void removerItem(Long solicitanteId, Long idPreset, Long idMaterial) {
        Funcionario solicitante = funcionarioService.buscarPorId(solicitanteId);
        exigirAdminOuGestor(solicitante);
        obterPresetObrigatorio(idPreset);

        PresetMaterialID id = new PresetMaterialID(idPreset, idMaterial);
        presetMaterialRepository.deleteById(id);
    }

    // ========== CONSULTAS ==========

    /**
     * Busca um Preset completo por ID.
     */
    @Transactional(readOnly = true)
    public PresetResponseDTO buscarPorId(Long idPreset) {
        Preset preset = obterPresetObrigatorio(idPreset);
        List<PresetMaterial> itens = presetMaterialRepository.findByIdIdPreset(idPreset);
        return toResponseDTO(preset, itens);
    }

    /**
     * Lista todos os Presets cadastrados.
     */
    @Transactional(readOnly = true)
    public List<PresetResponseDTO> listarTodos() {
        List<Preset> presets = presetRepository.findAll();

        return presets.stream()
                .map(p -> {
                    List<PresetMaterial> itens = presetMaterialRepository.findByIdIdPreset(p.getIdPreset());
                    return toResponseDTO(p, itens);
                })
                .collect(Collectors.toList());
    }

    /**
     * Lista apenas os Presets ativos.
     */
    @Transactional(readOnly = true)
    public List<PresetResponseDTO> listarAtivos() {
        List<Preset> presets = presetRepository.findByAtivo("S");
        return presets.stream()
                .map(p -> {
                    List<PresetMaterial> itens = presetMaterialRepository.findByIdIdPreset(p.getIdPreset());
                    return toResponseDTO(p, itens);
                })
                .collect(Collectors.toList());
    }

    // ========== IMPLEMENTAÇÕES INTERNAS ==========

    private void exigirAdminOuGestor(Funcionario f) {
        if (f == null || !( "ADMIN".equalsIgnoreCase(f.getCargo()) || "GESTOR".equalsIgnoreCase(f.getCargo()) ))
            throw new SecurityException("Ação restrita a ADMIN ou GESTOR.");
    }

    private void exigirCodigoUnico(String codigo, Long ignorarId) {
        Optional<Preset> existing = presetRepository.findByCodigoIgnoreCase(codigo);

        if (existing.isPresent() && !Objects.equals(existing.get().getIdPreset(), ignorarId)) {
            throw new IllegalArgumentException("Código de preset já cadastrado: " + codigo);
        }
    }

    private void validarMaterialExiste(Long idMaterial) {
        Material m = materialRepository.findById(idMaterial)
                .orElseThrow(() -> new NoSuchElementException("Material não encontrado: " + idMaterial));

        if ("N".equalsIgnoreCase(m.getAtivo())) {
            throw new IllegalStateException("Material inativo: " + idMaterial);
        }
    }

    private void validarQtdePorExame(BigDecimal qtde) {
        // Validação: qtde_por_exame deve ser > 0.
        if (qtde == null || qtde.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("A quantidade por exame deve ser maior que zero.");
    }

    private Preset obterPresetObrigatorio(Long id) {
        return presetRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Preset não encontrado: " + id));
    }

    private void removerTodosItens(Long idPreset) {
        presetMaterialRepository.deleteByIdIdPreset(idPreset);
    }

    private void definirItensInterno(Long idPreset, List<PresetItemRequestDTO> itens) {
        removerTodosItens(idPreset);

        for (PresetItemRequestDTO item : itens) {
            validarMaterialExiste(item.idMaterial());
            validarQtdePorExame(item.qtdePorExame());

            PresetMaterialID id = new PresetMaterialID(idPreset, item.idMaterial());
            PresetMaterial pm = new PresetMaterial(id, item.qtdePorExame());

            presetMaterialRepository.save(pm);
        }
    }

    // MAPPER (Entidade -> DTO)
    private PresetResponseDTO toResponseDTO(Preset preset, List<PresetMaterial> itens) {

        List<Long> materialIds = itens.stream().map(pm -> pm.getId().getIdMaterial()).collect(Collectors.toList());
        Map<Long, String> nomesMateriais = materialRepository.findAllById(materialIds).stream()
                .collect(Collectors.toMap(Material::getIdMaterial, Material::getNome));

        List<PresetItemResponseDTO> itensDto = itens.stream()
                .map(pm -> new PresetItemResponseDTO(
                        pm.getId().getIdMaterial(),
                        nomesMateriais.getOrDefault(pm.getId().getIdMaterial(), "Desconhecido"),
                        pm.getQtdePorExame()
                ))
                .collect(Collectors.toList());

        return new PresetResponseDTO(
                preset.getIdPreset(),
                preset.getNome(),
                preset.getCodigo(),
                preset.getDescricao(),
                preset.getAtivo(),
                itensDto
        );
    }
}