package com.stoq.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ESTOQUE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ESTOQUE")
    private Long idEstoque;

    @Column(name = "LABORATORIO_ID", nullable = false)
    @NotNull(message="ID do laboratório é obrigatório")
    private Long idLab;

    @Column(name = "MATERIAL_ID", nullable = false)
    @NotNull(message="ID do material é obrigatório")
    private Long idMaterial;

    @Column(name = "DIA", nullable = false)
    @NotNull(message = "É obrigatório informar a data")
    private LocalDate dia;

    @Column(name = "QTDE", nullable = false)
    @NotNull(message = "É obrigatório informar a quantidade atual")
    private BigDecimal quantidadeAtual;
}