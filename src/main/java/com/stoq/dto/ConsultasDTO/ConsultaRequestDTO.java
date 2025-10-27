package com.stoq.dto.ConsultaDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ConsultaRequestDTO(

        @NotBlank(message="Nome do paciente é obrigatório")
        String pacienteNome,

        @NotBlank(message="A documentação do paciente é obrigatória")
        String pacienteDoc,

        @NotNull(message="Horário e data da consulta são obrigatórios")
        LocalDate dataHora,

        // Status pode ter uma validação Pattern, mas vamos manter simples
        @NotBlank(message="O status da consulta é obrigatório")
        String status,

        String obs, // Observação pode ser nula

        @NotNull(message="ID do laboratório é obrigatório")
        Long idLab,

        @NotNull(message="ID do preset é obrigatório")
        Long idPreset
) {}