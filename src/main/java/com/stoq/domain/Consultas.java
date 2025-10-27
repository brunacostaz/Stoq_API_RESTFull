package com.stoq.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "CONSULTAS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consultas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CONSULTA")
    private Long idConsulta;

    @Column(name = "PACIENTE_NOME", nullable = false)
    @NotBlank(message="Nome do paciente é obrigatório")
    private String pacienteNome;

    @Column(name = "PACIENTE_DOC", nullable = false)
    @NotBlank(message="A documentação do paciente é obrigatório")
    private String pacienteDoc;

    @Column(name = "DATA_HORA", nullable = false)
    @NotNull(message="Horário e data da consulta são obrigatórios")
    private LocalDate dataHora;

    @Column(name = "STATUS", nullable = false)
    private String status;

    @Column(name = "OBS", nullable = false)
    private String obs;

    @Column(name = "DT_CRIACAO", nullable = false)
    @NotNull(message="Data de criação é obrigatório")
    private LocalDate dtCriacao;

    @Column(name = "LABORATORIO_ID", nullable = false)
    @NotNull(message="ID do laboratório é obrigatório")
    private Long idLab;

    @Column(name = "PRESET_ID", nullable = false)
    @NotNull(message="ID do preset é obrigatório")
    private Long idPreset;

    @Override
    public String toString() {
        return "Consulta {\n" +
                "  idConsulta=" + idConsulta + ",\n" +
                "  pacienteNome='" + pacienteNome + "',\n" +
                "  pacienteDoc='" + pacienteDoc + "',\n" +
                "  dataHora=" + dataHora + ",\n" +
                "  status='" + status + "',\n" +
                "  obs='" + obs + "',\n" +
                "  dtCriacao=" + dtCriacao + ",\n" +
                "  idLab=" + idLab + ",\n" +
                "  idPreset=" + idPreset + "\n" +
                "}";
    }
}