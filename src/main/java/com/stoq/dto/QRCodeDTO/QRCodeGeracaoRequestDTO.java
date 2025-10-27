package com.stoq.dto.QRCodeDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRCodeGeracaoRequestDTO {

    @NotNull(message = "O ID da consulta é obrigatório")
    private Long consultaId;

    @NotNull(message = "O ID do enfermeiro é obrigatório")
    private Long enfermeiroId;
}