package com.stoq.dto.QRCodeDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRCodeValidacaoRequestDTO {

    @NotNull(message = "O ID do administrador é obrigatório")
    private Long adminId;
}