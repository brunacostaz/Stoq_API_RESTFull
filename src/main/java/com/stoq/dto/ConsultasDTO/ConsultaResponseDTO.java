package com.stoq.dto.ConsultaDTO;

import java.time.LocalDate;

public record ConsultaResponseDTO(

        Long idConsulta,
        String pacienteNome,
        String pacienteDoc,
        LocalDate dataHora,
        String status,
        String obs,
        LocalDate dtCriacao,
        Long idLab,
        Long idPreset
) {}