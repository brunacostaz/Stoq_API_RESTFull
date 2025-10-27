package com.stoq.dto.PedidoDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

// NOVO NOME: PedidoCriacaoRequestDTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoCriacaoRequestDTO {

    private Long idLaboratorio;
    private Long solicitanteId;
    private List<Long> materiaisEmBaixa; // Lista de IDs de material
}