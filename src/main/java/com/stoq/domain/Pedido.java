package com.stoq.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "PEDIDOS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PEDIDO", nullable = false)
    private Long idPedido;

    @Column(name = "NUMERO", nullable = false)
    @NotBlank(message = "O número do pedido é obrigatório")
    private String numero;

    @Column(name = "LABORATORIO_ID", nullable = false)
    @NotNull(message = "O ID do laboratório é obrigatório")
    private Long idLaboratorio;

    @Column(name = "FUNCIONARIO_ID", nullable = false)
    @NotNull(message = "O ID do funcionário é obrigatório")
    private Long idFuncionario;

    @Column(name = "STATUS", nullable = false)
    @NotBlank(message = "O status do pedido é obrigatório")
    private String status;

    @Column(name = "DT_CRIACAO", nullable = false)
    @NotNull(message = "A data de criação é obrigatória")
    private LocalDate dtCriacao;

    @Column(name = "DT_RECEBIMENTO", nullable = true)
    private LocalDate dtRecebimento;

    @Column(name = "FORNECEDOR_NOME", nullable = true)
    private String fornecedorNome;

    @OneToMany(mappedBy = "id.idPedido", fetch = FetchType.LAZY)
    private List<PedidoItens> itens;

}