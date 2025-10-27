package com.stoq.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PRESETS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Preset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRESET", nullable = false)
    private Long idPreset;

    @Column(name = "NOME", nullable = false)
    @NotBlank(message = "O nome do Preset é obrigatório")
    private String nome;

    @Column(name = "CODIGO", nullable = false)
    @NotBlank(message = "O código do Preset é obrigatório")
    private String codigo;

    @Column(name = "DESCRICAO", nullable = false)
    @NotBlank(message = "A descrição do Preset é obrigatória")
    private String descricao;

    @Column(name = "ATIVO", nullable = false)
    @NotBlank(message = "O status 'ativo' é obrigatório ('S' ou 'N')")
    private String ativo; // CHAR(1 BYTE)
}