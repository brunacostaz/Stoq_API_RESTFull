package com.stoq.dto.FuncionarioDTO;

import java.time.LocalDate;

public record FuncionarioResponseDTO(

        Long idFuncionario,
        String nome,
        String cpf,
        String email,
        String cargo,
        Long idLaboratorio,
        String ativo,
        LocalDate dtCadastro
) {}