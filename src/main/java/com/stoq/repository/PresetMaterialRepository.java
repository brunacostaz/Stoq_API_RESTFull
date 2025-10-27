package com.stoq.repository;

import com.stoq.domain.PresetMaterial;
import com.stoq.domain.PresetMaterialID;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresetMaterialRepository extends JpaRepository<PresetMaterial, PresetMaterialID> {

    List<PresetMaterial> findByIdIdPreset(Long idPreset);

    @Query("SELECT pm.id.idMaterial FROM PresetMaterial pm WHERE pm.id.idPreset = ?1")
    List<Long> findMaterialIdsByPresetId(Long idPreset);

    // Método para exclusão em massa, mais eficiente que deletar um por um no loop
    @Modifying
    @Transactional
    void deleteByIdIdPreset(Long idPreset);
}