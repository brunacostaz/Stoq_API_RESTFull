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
@Table(name = "MOVIMENTACAO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MOVIMENTACAO")
    private Long idMovimentacao;

    @Column(name = "DIA_MOVIMENTACAO", nullable = false)
    @NotNull(message = "A data da movimentação é obrigatória")
    private LocalDate dataMovimentacao;

    @Column(name = "LABORATORIO_ID", nullable = false)
    @NotNull(message = "O ID do laboratório é obrigatório")
    private Long idLab;

    @Column(name = "MATERIAL_ID", nullable = false)
    @NotNull(message = "O ID do material é obrigatório")
    private Long idMaterial;

    @Column(name = "TIPO", nullable = false)
    @NotBlank(message = "O tipo de movimentação é obrigatório")
    private String tipo;

    @Column(name = "QTDE", nullable = false)
    @NotNull(message = "A quantidade é obrigatória")
    private BigDecimal qtde;

    @Column(name = "QRCODE_ID", nullable = false)
    @NotBlank(message = "O ID do QRCODE é obrigatório")
    private Long idQRCode;

    @Column(name = "FUNCIONARIO_ID", nullable = false)
    @NotBlank(message = "O ID funcionário é obrigatório")
    private Long idFuncionario;

    @Column(name = "OBS", nullable = true)
    private String obs;
}