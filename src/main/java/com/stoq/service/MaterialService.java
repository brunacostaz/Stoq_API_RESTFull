package com.stoq.service;

import com.stoq.domain.Material;
import com.stoq.dto.MaterialDTO.MaterialRequestDTO;
import com.stoq.dto.MaterialDTO.MaterialResponseDTO;
import com.stoq.repository.MaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class MaterialService {

    private final MaterialRepository repository;

    public MaterialService(MaterialRepository repository) {
        this.repository = repository;
    }


    @Transactional(readOnly = true)
    public List<MaterialResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MaterialResponseDTO> listarAtivos() {
        return repository.findByAtivo("S").stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MaterialResponseDTO buscarPorId(Long id) {
        Material material = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Material não encontrado com ID: " + id));
        return toResponseDTO(material);
    }


    @Transactional
    public MaterialResponseDTO cadastrar(MaterialRequestDTO request) {
        Material material = new Material();

        mapearRequestParaEntidade(material, request);

        Material salvo = repository.save(material);

        return toResponseDTO(salvo);
    }

    @Transactional
    public MaterialResponseDTO editar(Long id, MaterialRequestDTO request) {
        Material material = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Material não encontrado com ID: " + id));

        mapearRequestParaEntidade(material, request);

        Material atualizado = repository.save(material);

        return toResponseDTO(atualizado);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Material não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }


    // Mapper de Request DTO para Entidade
    private void mapearRequestParaEntidade(Material material, MaterialRequestDTO request) {
        material.setNome(request.nome());
        material.setIdLote(request.idLote());
        material.setUnidadeMedida(request.unidadeMedida());
        material.setEstoqueMinimo(request.estoqueMinimo());
        material.setDescricao(request.descricao());
        material.setAtivo(request.ativo());
    }

    // Mapper de Entidade para Response DTO
    private MaterialResponseDTO toResponseDTO(Material material) {
        return new MaterialResponseDTO(
                material.getIdMaterial(),
                material.getNome(),
                material.getIdLote(),
                material.getUnidadeMedida(),
                material.getEstoqueMinimo(),
                material.getDescricao(),
                material.getAtivo()
        );
    }
}