package com.stoq.mapper;

import com.stoq.domain.Pedido;
import com.stoq.domain.PedidoItens;
import com.stoq.dto.PedidoDTO.PedidoItemResponseDTO;
import com.stoq.dto.PedidoDTO.PedidoResponseDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PedidoMapper {

    // Mapeamento de Entidade (Pedido) para DTO de Resposta (PedidoResponseDTO)
    public static PedidoResponseDTO toResponseDTO(Pedido pedido) {
        if (pedido == null) return null;

        List<PedidoItemResponseDTO> itensResponse = Collections.emptyList();
        if (pedido.getItens() != null) {
            itensResponse = pedido.getItens().stream()
                    .map(PedidoMapper::toItemResponseDTO)
                    .collect(Collectors.toList());
        }

        return new PedidoResponseDTO(
                pedido.getIdPedido(),
                pedido.getNumero(),
                pedido.getIdLaboratorio(),
                pedido.getIdFuncionario(),
                pedido.getStatus(),
                pedido.getDtCriacao(),
                pedido.getDtRecebimento(),
                pedido.getFornecedorNome(),
                itensResponse
        );
    }

    // Mapeamento de PedidoItens para PedidoItemResponseDTO
    public static PedidoItemResponseDTO toItemResponseDTO(PedidoItens item) {
        return new PedidoItemResponseDTO(
                item.getId().getIdMaterial(),
                item.getQtdeSolicitada(),
                item.getQntdeRecebida(),
                item.getPrecoUnitario(),
                item.getLote(),
                item.getValidade()
        );
    }
}