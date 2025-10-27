package com.stoq.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "PEDIDO_ITENS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoItens {

    @EmbeddedId
    private PedidoItensID id;

    @Column(name = "QTDE_SOLICITADA", nullable = false)
    @NotNull(message = "A quantidade solicitada é obrigatória")
    private BigDecimal qtdeSolicitada;

    @Column(name = "QTDE_RECEBIDA", nullable = false)
    @NotNull(message = "A quantidade recebida é obrigatória")
    private BigDecimal qntdeRecebida;

    @Column(name = "PRECO_UNITARIO", nullable = false)
    @NotNull(message = "O preço unitário é obrigatório")
    private BigDecimal precoUnitario;

    @Column(name = "LOTE", nullable = true)
    private String lote;

    @Column(name = "VALIDADE", nullable = true)
    private LocalDate validade;


    public PedidoItens(Long idPedido, Long idMaterial, BigDecimal qtdeSolicitada, BigDecimal qntdeRecebida, BigDecimal precoUnitario, String lote, LocalDate validade) {
        this.id = new PedidoItensID(idPedido, idMaterial);
        this.qtdeSolicitada = qtdeSolicitada;
        this.qntdeRecebida = qntdeRecebida;
        this.precoUnitario = precoUnitario;
        this.lote = lote;
        this.validade = validade;
    }

}