package com.stoq.repository;

import com.stoq.domain.HistoricoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HistoricoEstoqueRepository extends JpaRepository<HistoricoEstoque, Long> {

    // 1. copiarEstoqueParaHistorico
    @Modifying
    @Transactional
    @Query(value = """
        MERGE INTO HISTORICO_ESTOQUE h
        USING (
            SELECT 
                :dia AS DIA_HISTORICO, -- Usando parâmetro nomeado
                e.LABORATORIO_ID,
                e.MATERIAL_ID,
                COALESCE(SUM(CASE WHEN m.TIPO = 'ENTRADA' THEN m.QTDE END), 0) AS QTDE_ENTRADAS,
                COALESCE(SUM(CASE WHEN m.TIPO = 'SAIDA'   THEN m.QTDE END), 0) AS QTDE_SAIDAS,
                MAX(e.QUANTIDADE_ATUAL) AS QTDE_FINAL
            FROM ESTOQUE e
            LEFT JOIN MOVIMENTACAO m
                   ON m.LABORATORIO_ID = e.ID_LAB
                  AND m.MATERIAL_ID    = e.ID_MATERIAL
                  AND TRUNC(m.DIA_MOVIMENTACAO) = :dia -- Usando parâmetro nomeado
            -- O filtro e.DIA = ?3 do seu DAO pode ter sido ignorado ou movido,
            -- mas garantimos que o mapeamento de data seja feito com :dia
            GROUP BY e.LABORATORIO_ID, e.MATERIAL_ID
        ) src
        ON (h.DIA_HISTORICO = src.DIA_HISTORICO
            AND h.LABORATORIO_ID = src.LABORATORIO_ID
            AND h.MATERIAL_ID    = src.MATERIAL_ID)
        WHEN MATCHED THEN
          UPDATE SET h.QTDE_FINAL   = src.QTDE_FINAL,
                     h.QTDE_ENTRADAS = src.QTDE_ENTRADAS,
                     h.QTDE_SAIDAS   = src.QTDE_SAIDAS
        WHEN NOT MATCHED THEN
          INSERT (DIA_HISTORICO, LABORATORIO_ID, MATERIAL_ID,
                  QTDE_INICIAL, QTDE_ENTRADAS, QTDE_SAIDAS, QTDE_AJUSTES, QTDE_FINAL)
          VALUES (src.DIA_HISTORICO, src.LABORATORIO_ID, src.MATERIAL_ID,
                  0, src.QTDE_ENTRADAS, src.QTDE_SAIDAS, 0, src.QTDE_FINAL)
    """, nativeQuery = true)
    void copiarEstoqueParaHistorico(@Param("dia") LocalDate dia);


    // 2. findByLaboratorio
    List<HistoricoEstoque> findByIdLaboratorioOrderByDiaHistoricoDesc(Long idLaboratorio);
}