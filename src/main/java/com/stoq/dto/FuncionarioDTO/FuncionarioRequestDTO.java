package com.stoq.dto.FuncionarioDTO;

import jakarta.validation.constraints.*;

public record FuncionarioRequestDTO(

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O CPF é obrigatório")
        @Size(min = 11, max = 11, message = "O CPF deve ter 11 dígitos") // Assumindo CPF simples
        String cpf,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail deve ser válido")
        String email,

        @NotBlank(message = "O cargo é obrigatório")
        @Pattern(regexp = "ADMIN|GESTOR|ENFERMEIRO", message = "Cargo inválido. Use: ADMIN, GESTOR ou ENFERMEIRO.")
        String cargo,

        @NotNull(message = "O ID do laboratório é obrigatório")
        Long idLaboratorio,

        @Pattern(regexp = "^[SN]$", message = "O status ATIVO deve ser 'S' ou 'N'.")
        String ativo // Pode vir null, mas se vier, validamos o formato
) {}