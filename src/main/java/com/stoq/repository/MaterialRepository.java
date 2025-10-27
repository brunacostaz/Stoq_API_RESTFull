package com.stoq.repository;

import com.stoq.domain.Laboratorio;
import com.stoq.domain.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    @Query("SELECT m FROM Material m JOIN Estoque e ON m.idMaterial = e.idMaterial WHERE e.idLab = :laboratorioId AND e.quantidadeAtual <= m.estoqueMinimo")
    List<Material> findMateriaisAbaixoEstoqueMinimo(@Param("laboratorioId") Long laboratorioId);

    List<Material> findByAtivo(String ativo);

    // Comparação entre estoque mínimo e uso real
    @Query(value = """
        SELECT m.ID_MATERIAL, m.ESTOQUE_MINIMO, NVL(SUM(mov.QTDE),0) AS USO_REAL 
        FROM MATERIAIS m 
        LEFT JOIN MOVIMENTACAO mov ON m.ID_MATERIAL = mov.MATERIAL_ID AND mov.TIPO = 'SAIDA' 
        GROUP BY m.ID_MATERIAL, m.ESTOQUE_MINIMO
    """, nativeQuery = true)
    List<Object[]> getComparacaoEstoqueMinimoUso();
}