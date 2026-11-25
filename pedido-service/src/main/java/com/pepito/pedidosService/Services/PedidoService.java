package com.pepito.pedidosService.Services;


import com.pepito.pedidosService.DTOs.Producto;
import com.pepito.pedidosService.Models.Pedido;
import com.pepito.pedidosService.Repositories.PedidoRepository;
import com.pepito.pedidosService.config.PedidoEventPublisher;
import com.pepito.pedidosService.config.ProductoClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import principalcommons.PedidoEvent;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {
    @Autowired
    private PedidoRepository pedidoRepository;
    //@Autowired
    //private RestTemplate restTemplate;
    @Autowired
    private final PedidoEventPublisher pedidoEventPublisher;
    private final ProductoClient productoClient;
    
    @CircuitBreaker(name = "productoServiceCB", fallbackMethod = "fallbackGetProducto")
    public Pedido registrarPedido(Pedido pedido) {
        log.info("Iniciando registro de pedido. idProducto={}, cantidad={}", pedido.getIdProducto(), pedido.getCantidad());
        // Llamada al microservicio de productos
        //String productoURL = "http://localhost:7070/api/v1/productos/"+pedido.getIdProducto();
        //Producto producto = restTemplate.getForObject(productoURL, Producto.class);
        log.debug("Consultando producto en producto-service. idProducto={}", pedido.getIdProducto());
        Producto producto = productoClient.obtenerProductoPorId(pedido.getIdProducto());
        if (producto == null) {
            log.error("No se encontró el producto en producto-service. idProducto={}", pedido.getIdProducto());
            throw new RuntimeException("No se encontró el producto (conexión REST a microservicio producto-service)");
        }
        log.info("Producto obtenido correctamente. idProducto={}, precio={}", pedido.getIdProducto(), producto.getPrecio());
        pedido.setTotal(producto.getPrecio() * pedido.getCantidad());
        log.debug("Total calculado para el pedido. idProducto={}, cantidad={}, total={}", pedido.getIdProducto(), pedido.getCantidad(), pedido.getTotal());

        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        log.info("Pedido guardado correctamente. idPedido={}, idProducto={}, cantidad={}, total={}", pedidoGuardado.getId(), pedidoGuardado.getIdProducto(), pedidoGuardado.getCantidad(), pedidoGuardado.getTotal());
        pedidoEventPublisher.publishPedidoCreatedEvent(
                new PedidoEvent(pedidoGuardado.getId(), pedidoGuardado.getIdProducto(), pedidoGuardado.getCantidad())
        );
        log.info("Evento de pedido creado publicado correctamente. idPedido={}", pedidoGuardado.getId());

        return pedidoGuardado;
    }
    public List<Pedido> obtenerTodosLosPedidos(){
        log.info("Consultando todos los pedidos");
        List<Pedido> pedidos = pedidoRepository.findAll();
        log.info("Consulta de pedidos finalizada. cantidad={}", pedidos.size());
        return pedidos;
    }

    public Pedido fallbackGetProducto(Pedido pedido, Throwable ex){
        log.error("Fallback ejecutado al obtener producto. idProducto={}, cantidad={}, motivo={}", pedido.getIdProducto(), pedido.getCantidad(), ex.getMessage(), ex);
        Pedido pedidoFallback = new Pedido();
        pedidoFallback.setIdProducto(pedido.getIdProducto());
        pedidoFallback.setCantidad(pedido.getCantidad());
        pedidoFallback.setTotal(-1.0);
        Pedido pedidoGuardado = pedidoRepository.save(pedidoFallback);
        log.warn("Pedido fallback guardado con total inválido. idPedido={}, idProducto={}, cantidad={}, total={}", pedidoGuardado.getId(), pedidoGuardado.getIdProducto(), pedidoGuardado.getCantidad(), pedidoGuardado.getTotal());
        return pedidoGuardado;
    }
}
