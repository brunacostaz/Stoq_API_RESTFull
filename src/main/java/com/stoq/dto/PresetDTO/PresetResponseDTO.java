package com.stoq.dto.PresetDTO;

import java.util.List;

public record PresetResponseDTO(

        Long idPreset,
        String nome,
        String codigo,
        String descricao,
        String ativo,

        List<PresetItemResponseDTO> itens
) {}