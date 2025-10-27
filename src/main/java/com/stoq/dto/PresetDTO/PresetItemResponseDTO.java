package com.stoq.dto.PresetDTO;

import java.math.BigDecimal;

public record PresetItemResponseDTO(

        Long idMaterial,
        String nomeMaterial,
        BigDecimal qtdePorExame
) {}