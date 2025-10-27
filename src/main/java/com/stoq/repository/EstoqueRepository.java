package com.stoq.repository;

import com.stoq.domain.Material;
import com.stoq.domain.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    // 1. getQtdeAtual
    @Query("SELECT e.quantidadeAtual FROM Estoque e WHERE e.idLab = :idLab AND e.idMaterial = :idMaterial ORDER BY e.dia DESC FETCH FIRST 1 ROWS ONLY")
    BigDecimal findQuantidadeAtual(Long idLab, Long idMaterial);


    // 2. atualizarEstoque (MERGE)
    @Modifying
    @Transactional
    @Query(value = """
        MERGE INTO ESTOQUE e
        USING (SELECT 1 FROM DUAL) src
        ON (e.LABORATORIO_ID = :idLab AND e.MATERIAL_ID = :idMaterial AND e.DIA = TRUNC(SYSDATE))
        WHEN MATCHED THEN
          UPDATE SET e.QTDE = e.QTDE + :qtde
        WHEN NOT MATCHED THEN
          INSERT (LABORATORIO_ID, MATERIAL_ID, DIA, QTDE)
          VALUES (:idLab, :idMaterial, TRUNC(SYSDATE), :qtde)
    """, nativeQuery = true)
    void atualizarEstoque(Long idLab, Long idMaterial, BigDecimal qtde);


    // 3. limparDiasAntigos
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM ESTOQUE WHERE DIA < :diaAtual", nativeQuery = true)
    void limparDiasAntigos(LocalDate diaAtual);


    // 4. findMateriaisProximosVencimento (Ajustado para JPQL)
    @Query("""
        SELECT DISTINCT m
        FROM Material m
        JOIN PedidoItens pi ON m.idMaterial = pi.id.idMaterial
        JOIN Pedido p ON pi.id.idPedido = p.idPedido
        WHERE p.idLaboratorio = :laboratorioId
          AND pi.validade IS NOT NULL
          AND pi.validade <= function('ADD_DAYS', CURRENT_DATE(), :diasAviso)
    """)
    List<Material> findMateriaisProximosVencimento(Long laboratorioId, Integer diasAviso);


    // 5. findByLaboratorio (Busca todo o estoque de um laboratório)
    List<Estoque> findByIdLabOrderByDiaDesc(Long idLab);
}