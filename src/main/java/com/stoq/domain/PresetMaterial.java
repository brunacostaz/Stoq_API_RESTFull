package com.stoq.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "PRESET_MATERIAIS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresetMaterial {

    @EmbeddedId
    private PresetMaterialID id;

    @Column(name = "QTDE_POR_EXAME", nullable = false)
    @NotNull(message = "A quantidade por exame é obrigatória")
    private BigDecimal qtdePorExame;

    public PresetMaterial(Long idPreset, Long idMaterial, BigDecimal qtdePorExame) {
        this.id = new PresetMaterialID(idPreset, idMaterial);
        this.qtdePorExame = qtdePorExame;
    }
}