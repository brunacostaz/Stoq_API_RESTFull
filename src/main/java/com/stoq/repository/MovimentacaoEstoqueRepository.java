package com.stoq.repository;

import com.stoq.domain.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    List<MovimentacaoEstoque> findByIdLabOrderByDataMovimentacaoDesc(Long idLab);

    // Retirada e reposição ao longo do tempo (Retorna um Object Array que será mapeado no Service)
    @Query(value = "SELECT TRUNC(DIA_MOVIMENTACAO) AS DIA, TIPO, SUM(QTDE) AS TOTAL FROM MOVIMENTACAO GROUP BY TRUNC(DIA_MOVIMENTACAO), TIPO", nativeQuery = true)
    List<Object[]> getMovimentacoesAoLongoDoTempo();

    // Frequência de retirada por enfermeiro
    @Query(value = "SELECT FUNCIONARIO_ID, COUNT(ID_MOVIMENTACAO) AS TOTAL FROM MOVIMENTACAO WHERE TIPO = 'SAIDA' AND FUNCIONARIO_ID IS NOT NULL GROUP BY FUNCIONARIO_ID", nativeQuery = true)
    List<Object[]> getFrequenciaPorEnfermeiro();

    // Materiais mais utilizados na semana
    @Query(value = "SELECT MATERIAL_ID, SUM(QTDE) AS TOTAL FROM MOVIMENTACAO WHERE TIPO = 'SAIDA' AND DIA_MOVIMENTACAO >= TRUNC(SYSDATE) - 7 GROUP BY MATERIAL_ID ORDER BY TOTAL DESC", nativeQuery = true)
    List<Object[]> getMateriaisMaisUsadosNaSemana();

    // Dias/horários de maior movimento
    @Query(value = "SELECT TO_CHAR(DIA_MOVIMENTACAO, 'DY') AS DIA, COUNT(ID_MOVIMENTACAO) AS TOTAL FROM MOVIMENTACAO GROUP BY TO_CHAR(DIA_MOVIMENTACAO, 'DY')", nativeQuery = true)
    List<Object[]> getMovimentoPorDiaDaSemana();
}