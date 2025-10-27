package com.stoq.dto.EstoqueDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstoqueResponseDTO {

    private Long idEstoque;
    private Long idLab;
    private Long idMaterial;
    private LocalDate dia;
    private BigDecimal quantidadeAtual;
}