package com.stoq.mapper;

import com.stoq.domain.Estoque;
import com.stoq.dto.EstoqueDTO.EstoqueResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class EstoqueMapper {

    public static EstoqueResponseDTO toResponseDTO(Estoque estoque) {
        if (estoque == null) return null;

        return new EstoqueResponseDTO(
                estoque.getIdEstoque(),
                estoque.getIdLab(),
                estoque.getIdMaterial(),
                estoque.getDia(),
                estoque.getQuantidadeAtual()
        );
    }

    public static List<EstoqueResponseDTO> toResponseDTOList(List<Estoque> estoqueList) {
        return estoqueList.stream()
                .map(EstoqueMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}