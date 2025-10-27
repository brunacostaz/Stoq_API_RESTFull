package com.stoq.repository;

import com.stoq.domain.Laboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Long> {
    List<Laboratorio> findByAtivo(String ativo); // busca por 'S' ou 'N'
}
