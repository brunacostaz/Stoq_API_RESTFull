package com.stoq.dto.PedidoDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

// NOVO NOME: PedidoRecebimentoRequestDTO - Representa um item recebido
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRecebimentoRequestDTO {

    private BigDecimal qntdeRecebida;
    private BigDecimal precoUnitario;
    private String lote;
    private LocalDate validade; // Data no formato YYYY-MM-DD
}