package com.stoq.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "FUNCIONARIOS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario {

    @Id
    @Column(name = "ID_FUNCIONARIO", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFuncionario;

    @Column(name = "NOME", nullable = false)
    @NotBlank(message="Nome do funcionário é obrigatório")
    private String nome;

    @Column(name = "CPF", nullable = false)
    @NotBlank(message="CPF do funcionário é obrigatório")
    private String cpf;

    @Column(name = "EMAIL", nullable = false)
    @NotBlank(message="Email do funcionário é obrigatório")
    private String email;

    @Column(name = "CARGO", nullable = false)
    @NotBlank(message="Cargo do funcionário é obrigatório")
    private String cargo;

    @Column(name = "ATIVO", nullable = false)
    private String ativo;

    @Column(name = "DT_CADASTRO", nullable = false)
    @NotBlank(message="Data do cadastro é obrigatório")
    private LocalDate dtCadastro;

    @Column(name = "LABORATORIO_ID", nullable = false)
    @NotBlank(message="ID do laboratório do funcionário é obrigatório")
    private Long idLaboratorio;

    @Override
    public String toString() {
        return "Funcionario {\n" +
                "  idFuncionario=" + idFuncionario + ",\n" +
                "  nome='" + nome + "',\n" +
                "  cpf='" + cpf + "',\n" +
                "  email='" + email + "',\n" +
                "  cargo='" + cargo + "',\n" +
                "  ativo='" + ativo + "',\n" +
                "  dtCadastro=" + dtCadastro + ",\n" +
                "  idLaboratorio=" + idLaboratorio + "\n" +
                "}";
    }
}
