package com.stoq.dto.QRCodeDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRCodeResponseDTO {

    private Long idQRCode;
    private Long idConsulta;
    private Long idEnfermeiro;
    private Long idAdminValidador;
    private Long idLaboratorio;
    private String codigo;
    private String status;
    private LocalDate dtGeracao;
    private LocalDate dtValidacao;
}