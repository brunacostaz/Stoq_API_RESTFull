package com.stoq.dto.AnalyticsDTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UsoMaterialDTO {
    private Long idMaterial;
    private BigDecimal totalUso;
}