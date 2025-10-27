package com.stoq.mapper;

import com.stoq.domain.QRCode;
import com.stoq.dto.QRCodeDTO.QRCodeResponseDTO;

public class QRCodeMapper {

    public static QRCodeResponseDTO toResponseDTO(QRCode qrCode) {
        if (qrCode == null) return null;

        return new QRCodeResponseDTO(
                qrCode.getIdQRCode(),
                qrCode.getIdConsulta(),
                qrCode.getIdEnfermeiro(),
                qrCode.getIdAdminValidador(),
                qrCode.getIdLaboratorio(),
                qrCode.getCodigo(),
                qrCode.getStatus(),
                qrCode.getDtGeracao(),
                qrCode.getDtValidacao()
        );
    }
}