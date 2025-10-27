package com.stoq.dto.AnalyticsDTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MovimentacaoTempoDTO {
    private LocalDate dia;
    private String tipo;
    private BigDecimal total;
}