package com.stoq.repository;

import com.stoq.domain.Pedido;
import com.stoq.domain.PedidoItens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT pi FROM PedidoItens pi WHERE pi.id.idPedido = :pedidoId")
    List<PedidoItens> buscarItensDoPedido(Long pedidoId);

    // Tempo médio entre pedido e recebimento (Retorna um Double)
    @Query(value = "SELECT AVG(DT_RECEBIMENTO - DT_CRIACAO) FROM PEDIDOS WHERE DT_RECEBIMENTO IS NOT NULL", nativeQuery = true)
    Double getTempoMedioEntregaPedidos();
}