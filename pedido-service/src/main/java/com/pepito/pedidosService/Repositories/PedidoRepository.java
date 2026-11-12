package com.pepito.pedidosService.Repositories;

import com.pepito.pedidosService.Models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
