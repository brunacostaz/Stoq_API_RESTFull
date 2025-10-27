package com.stoq.dto.PresetDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PresetRequestDTO(

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O código é obrigatório")
        String codigo,

        String descricao,

        @Pattern(regexp = "^[SN]$", message = "O status ATIVO deve ser 'S' ou 'N'.")
        String ativo
) {}