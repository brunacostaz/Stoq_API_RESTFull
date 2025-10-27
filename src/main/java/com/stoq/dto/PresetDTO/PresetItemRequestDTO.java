package com.stoq.dto.PresetDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record PresetItemRequestDTO(

        @NotNull(message = "O ID do material é obrigatório")
        Long idMaterial,

        @NotNull(message = "A quantidade por exame é obrigatória")
        @DecimalMin(value = "0.001", message = "A quantidade deve ser maior que zero")
        BigDecimal qtdePorExame
) {}