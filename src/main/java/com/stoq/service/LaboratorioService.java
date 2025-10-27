package com.stoq.service;

import com.stoq.domain.Laboratorio;
import com.stoq.dto.LaboratorioDTO.CadastrarLaboratorioRequestDTO;
import com.stoq.dto.LaboratorioDTO.CadastrarLaboratorioResponseDTO;
import com.stoq.repository.LaboratorioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class LaboratorioService {

    private final LaboratorioRepository repo;

    public LaboratorioService(LaboratorioRepository repo) {
        this.repo = repo;
    }


    @Transactional(readOnly = true)
    public List<CadastrarLaboratorioResponseDTO> listar() {
        return repo.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CadastrarLaboratorioResponseDTO> listarAtivos() {
        return repo.findByAtivo("S").stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public CadastrarLaboratorioResponseDTO cadastrar(CadastrarLaboratorioRequestDTO request) {
        Laboratorio lab = new Laboratorio();
        lab.setNome(request.nome());
        lab.setCodigo(request.codigo());
        lab.setAtivo(request.ativo());
        lab.setDtCadastro(LocalDate.now());

        Laboratorio salvo = repo.save(lab);

        return toResponseDTO(salvo);
    }

    @Transactional
    public CadastrarLaboratorioResponseDTO editar(Long id, CadastrarLaboratorioRequestDTO request) {
        Laboratorio lab = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Laboratório não encontrado com ID: " + id));

        lab.setNome(request.nome());
        lab.setCodigo(request.codigo());
        lab.setAtivo(request.ativo());


        Laboratorio atualizado = repo.save(lab);

        return toResponseDTO(atualizado); // 4. Reuso do método Mapper
    }

    @Transactional
    public void deletar(Long id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Laboratório não encontrado com ID: " + id);
        }
        repo.deleteById(id);
    }


    private CadastrarLaboratorioResponseDTO toResponseDTO(Laboratorio lab) {
        return new CadastrarLaboratorioResponseDTO(
                lab.getIdLab(),
                lab.getNome(),
                lab.getCodigo(),
                lab.getAtivo(),
                lab.getDtCadastro()
        );
    }

    @Transactional(readOnly = true)
    public CadastrarLaboratorioResponseDTO buscarPorId(Long id) {
        Laboratorio lab = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Laboratório não encontrado com ID: " + id));

        return toResponseDTO(lab);
    }
}