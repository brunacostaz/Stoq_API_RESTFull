package com.stoq.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "QRCODE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_QRCODE", nullable = false)
    private Long idQRCode;

    @Column(name = "CONSULTA_ID", nullable = false)
    @NotNull(message = "O ID da consulta é obrigatório")
    private Long idConsulta;

    @Column(name = "ENFERMEIRO_ID", nullable = false)
    @NotNull(message = "O ID do enfermeiro é obrigatório")
    private Long idEnfermeiro;

    @Column(name = "ADMIN_VALIDADOR_ID", nullable = false)
    @NotNull(message = "O ID do administrador validador é obrigatório")
    private Long idAdminValidador;

    @Column(name = "LABORATORIO_ID", nullable = false)
    @NotNull(message = "O ID do laboratório é obrigatório")
    private Long idLaboratorio;

    @Column(name = "CODIGO", nullable = false)
    @NotBlank(message = "O código é obrigatório")
    private String codigo;

    @Column(name = "STATUS", nullable = false)
    @NotBlank(message = "O status é obrigatório")
    private String status;

    @Column(name = "DT_GERACAO", nullable = false)
    @NotNull(message = "A data de geração é obrigatória")
    private LocalDate dtGeracao;

    @Column(name = "DT_VALIDACAO", nullable = false)
    @NotNull(message = "A data de validação é obrigatória")
    private LocalDate dtValidacao;
}