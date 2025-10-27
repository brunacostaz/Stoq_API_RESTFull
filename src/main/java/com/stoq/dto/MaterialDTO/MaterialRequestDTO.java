package com.stoq.dto.MaterialDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record MaterialRequestDTO(

        @NotBlank(message = "O nome do material é obrigatório")
        String nome,

        @NotBlank(message = "O ID do lote é obrigatório")
        String idLote,

        @NotBlank(message = "A unidade de medida é obrigatória")
        String unidadeMedida,

        @NotNull(message = "O estoque mínimo é obrigatório")
        @Min(value = 0, message = "O estoque mínimo não pode ser negativo")
        Integer estoqueMinimo,

        // Descrição pode ser nula
        String descricao,

        @NotBlank(message = "O status de ativo é obrigatório ('S' ou 'N')")
        @Pattern(regexp = "^[SN]$", message = "O status ATIVO deve ser 'S' ou 'N'")
        String ativo
) {}