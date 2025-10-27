package com.stoq.dto.PedidoDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {

    private Long idPedido;
    private String numero;
    private Long idLaboratorio;
    private Long idFuncionario;
    private String status;
    private LocalDate dtCriacao;
    private LocalDate dtRecebimento;
    private String fornecedorNome;
    private List<PedidoItemResponseDTO> itens;
}