package com.stoq.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MATERIAIS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MATERIAL")
    private Long idMaterial;

    @Column(name = "NOME", nullable = false)
    @NotBlank(message = "O nome do material é obrigatório")
    private String nome;

    @Column(name = "ID_LOTE", nullable = false)
    @NotBlank(message = "O ID do lote é obrigatório")
    private String idLote;

    @Column(name = "UNIDADE_MEDIDA", nullable = false)
    @NotBlank(message = "A unidade de medida é obrigatória")
    private String unidadeMedida;

    @Column(name = "ESTOQUE_MINIMO", nullable = false)
    @NotNull(message = "O estoque mínimo é obrigatório")
    @Min(value = 0, message = "O estoque mínimo não pode ser negativo")
    private Integer estoqueMinimo;

    @Column(name = "DESCRICAO", nullable = true)
    private String descricao;

    @Column(name = "ATIVO", nullable = false)
    @NotBlank(message = "O status de ativo é obrigatório ('S' ou 'N')")
    private String ativo; // 'S' ou 'N'

}