package com.stoq.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoItensID implements Serializable {

    @Column(name = "PEDIDO_ID")
    private Long idPedido;

    @Column(name = "MATERIAL_ID")
    private Long idMaterial;
}