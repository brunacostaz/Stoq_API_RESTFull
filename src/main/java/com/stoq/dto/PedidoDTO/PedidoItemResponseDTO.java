package com.stoq.dto.PedidoDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

// NOVO NOME: PedidoItemResponseDTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoItemResponseDTO {

    private Long idMaterial;
    private BigDecimal qtdeSolicitada;
    private BigDecimal qntdeRecebida;
    private BigDecimal precoUnitario;
    private String lote;
    private LocalDate validade;
}