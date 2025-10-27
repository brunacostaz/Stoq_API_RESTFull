package com.stoq.dto.MaterialDTO;

// Usamos os tipos Wrapper para IDs e o tipo Integer para estoque
public record MaterialResponseDTO(

        Long idMaterial,
        String nome,
        String idLote,
        String unidadeMedida,
        Integer estoqueMinimo,
        String descricao,
        String ativo
) {}