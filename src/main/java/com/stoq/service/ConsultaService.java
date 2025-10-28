package com.stoq.service;

import com.stoq.domain.Consultas;
import com.stoq.dto.ConsultaDTO.ConsultaRequestDTO;
import com.stoq.dto.ConsultaDTO.ConsultaResponseDTO;
import com.stoq.repository.ConsultaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ConsultaService {

    private final ConsultaRepository repository;

    public ConsultaService(ConsultaRepository repository) {
        this.repository = repository;
    }

    // =================== MÉTODOS DE LEITURA (READ) ===================

    @Transactional(readOnly = true)
    public List<ConsultaResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConsultaResponseDTO buscarPorId(Long id) {
        Consultas consulta = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Consulta não encontrada com ID: " + id));
        return toResponseDTO(consulta);
    }

    // =================== MÉTODOS DE ESCRITA (CREATE, UPDATE, DELETE) ===================

    @Transactional
    public ConsultaResponseDTO cadastrar(ConsultaRequestDTO request) {
        Consultas consulta = new Consultas();
        mapearRequestParaEntidade(consulta, request);

        consulta.setDtCriacao(LocalDate.now());

        Consultas salvo = repository.save(consulta);
        return toResponseDTO(salvo);
    }

    @Transactional
    public ConsultaResponseDTO editar(Long id, ConsultaRequestDTO request) {
        Consultas consulta = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Consulta não encontrada com ID: " + id));

        mapearRequestParaEntidade(consulta, request);

        Consultas atualizada = repository.save(consulta);
        return toResponseDTO(atualizada);
    }


    // =================== MÉTODOS AUXILIARES (MAPPER) ===================

    // NOVO MÉTODO AUXILIAR QUE ESTAVA FALTANDO!
    private void mapearRequestParaEntidade(Consultas consulta, ConsultaRequestDTO request) {
        consulta.setPacienteNome(request.pacienteNome());
        consulta.setPacienteDoc(request.pacienteDoc());
        consulta.setDataHora(request.dataHora());
        consulta.setStatus(request.status());
        consulta.setObs(request.obs());
        consulta.setIdLab(request.idLab());
        consulta.setIdPreset(request.idPreset());
    }

    // Mapper de Entidade para Response DTO
    private ConsultaResponseDTO toResponseDTO(Consultas consulta) {
        return new ConsultaResponseDTO(
                consulta.getIdConsulta(),
                consulta.getPacienteNome(),
                consulta.getPacienteDoc(),
                consulta.getDataHora(),
                consulta.getStatus(),
                consulta.getObs(),
                consulta.getDtCriacao(),
                consulta.getIdLab(),
                consulta.getIdPreset()
        );
    }
}