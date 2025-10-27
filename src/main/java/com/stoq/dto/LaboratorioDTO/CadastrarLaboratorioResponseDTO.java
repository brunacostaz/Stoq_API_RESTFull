package com.stoq.dto.LaboratorioDTO;

import java.time.LocalDate;

public record CadastrarLaboratorioResponseDTO(
        Long idLab,
        String nome,
        String codigo,
        String ativo,
        LocalDate dtCadastro
) {}
