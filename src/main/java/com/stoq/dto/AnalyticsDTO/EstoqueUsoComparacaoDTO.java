package com.stoq.dto.AnalyticsDTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class EstoqueUsoComparacaoDTO {
    private Long idMaterial;
    private BigDecimal estoqueMinimo;
    private BigDecimal usoReal;
    private BigDecimal diferenca; // UsoReal - EstoqueMinimo
}