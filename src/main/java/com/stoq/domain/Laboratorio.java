package com.stoq.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "LABORATORIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Laboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LABORATORIO")
    private Long idLab;

    @Column(name = "NOME", nullable = false)
    @NotBlank(message="ID do laboratório é obrigatório")
    private String nome;

    @Column(name = "CODIGO", nullable = false)
    @NotBlank(message="ID do laboratório é obrigatório")
    private String codigo;

    @Column(name = "ATIVO", nullable = false)
    @NotBlank(message="ID do laboratório é obrigatório")
    private String ativo;   // 'S' ou 'N'

    @Column(name = "DT_CADASTRO", nullable = false)
    @NotNull(message="Data de cadastro é obrigatória")
    private LocalDate dtCadastro;

    @Override
    public String toString() {
        return "{\n" +
                "  \"idLab\": " + idLab + ",\n" +
                "  \"nome\": \"" + nome + "\",\n" +
                "  \"codigo\": \"" + codigo + "\",\n" +
                "  \"ativo\": \"" + ativo + "\",\n" +
                "  \"dtCadastro\": \"" + dtCadastro + "\"\n" +
                "}";
    }

}
