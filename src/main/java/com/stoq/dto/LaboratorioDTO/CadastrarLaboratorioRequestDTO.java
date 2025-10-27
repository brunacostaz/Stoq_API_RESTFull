package com.stoq.dto.LaboratorioDTO;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CadastrarLaboratorioRequestDTO(
        @NotBlank String nome,
        @NotBlank String codigo,
        @NotBlank String ativo, // 'S' ou 'N'
        LocalDate dtCadastro
) {}
