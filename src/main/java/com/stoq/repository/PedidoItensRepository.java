package com.stoq.repository;

import com.stoq.domain.PedidoItens;
import com.stoq.domain.PedidoItensID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoItensRepository extends JpaRepository<PedidoItens, PedidoItensID> {

    List<PedidoItens> findByIdIdPedido(Long idPedido);

    // Taxa de materiais vencidos descartados por mês
    @Query(value = "SELECT TO_CHAR(VALIDADE, 'MM-YYYY') AS MES, COUNT(*) AS TOTAL FROM PEDIDO_ITENS WHERE VALIDADE < SYSDATE GROUP BY TO_CHAR(VALIDADE, 'MM-YYYY') ORDER BY MES", nativeQuery = true)
    List<Object[]> getMateriaisVencidosPorMes();
}