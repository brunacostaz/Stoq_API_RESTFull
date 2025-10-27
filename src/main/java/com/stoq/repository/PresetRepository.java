package com.stoq.repository;

import com.stoq.domain.Preset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PresetRepository extends JpaRepository<Preset, Long> {

    List<Preset> findByAtivo(String ativo);

    Optional<Preset> findByCodigoIgnoreCase(String codigo);
}