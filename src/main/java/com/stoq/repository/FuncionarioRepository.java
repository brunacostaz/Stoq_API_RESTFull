package com.stoq.repository;

import com.stoq.domain.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    // Para buscar por e-mail (usado na checagem de unicidade)
    Optional<Funcionario> findByEmailIgnoreCase(String email);

    // Para listar por laboratório
    List<Funcionario> findByIdLaboratorio(Long idLaboratorio);

    // Para listar ativos por cargo
    List<Funcionario> findByAtivoAndCargoIgnoreCase(String ativo, String cargo);
}