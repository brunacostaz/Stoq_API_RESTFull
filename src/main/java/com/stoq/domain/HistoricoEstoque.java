package com.stoq.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull; // Ajustado para NotNull
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "HISTORICO_ESTOQUE")
@Data // Gera Getters, Setters e o toString padrão
@NoArgsConstructor // Gera o construtor vazio
@AllArgsConstructor // Gera o construtor com todos os campos
public class HistoricoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HISTORICO")
    private Long idHistorico;

    @Column(name = "DIA_HISTORICO", nullable = false)
    @NotNull(message="O dia do histórico é obrigatório")
    private LocalDate diaHistorico;

    @Column(name = "LABORATORIO_ID", nullable = false)
    @NotNull(message="O ID do laboratório é obrigatório")
    private Long idLaboratorio;

    @Column(name = "MATERIAL_ID", nullable = false)
    @NotNull(message="O ID do material é obrigatório")
    private Long idMaterial;

    @Column(name = "QTDE_INICIAL", nullable = false)
    @NotNull(message="A quantidade inicial é obrigatória")
    private Float qtdeInicial;

    @Column(name = "QTDE_ENTRADAS", nullable = false) // Nome da coluna ajustado
    @NotNull(message="A quantidade de entradas é obrigatória")
    private Float qtdeEntradas;

    @Column(name = "QTDE_SAIDAS", nullable = false) // Nome da coluna ajustado
    @NotNull(message="A quantidade de saídas é obrigatória")
    private Float qtdeSaidas;

    @Column(name = "QTDE_AJUSTES", nullable = false) // Nome da coluna ajustado
    @NotNull(message="A quantidade de ajustes é obrigatória")
    private Float qtdeAjustes;

    @Column(name = "QTDE_FINAL", nullable = false) // Nome da coluna ajustado
    @NotNull(message="A quantidade final é obrigatória")
    private Float qtdeFinal;
}