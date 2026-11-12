package com.pepito.pedidosService.Services;


import com.pepito.pedidosService.DTOs.Producto;
import com.pepito.pedidosService.Models.Pedido;
import com.pepito.pedidosService.Repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PedidoService {
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private RestTemplate restTemplate;
    public Pedido registrarPedido(Pedido pedido) {
        // Llamada al microservicio de productos
        String productoURL = "http://localhost:7070/api/v1/productos/"+pedido.getIdProducto();
        Producto producto = restTemplate.getForObject(productoURL, Producto.class);
        if(producto == null) throw new RuntimeException("No se encontró el producto (conexión REST a microservicio producto-service)");
        pedido.setTotal(producto.getPrecio()*pedido.getCantidad());
        return pedidoRepository.save(pedido);
    }
    public List<Pedido> obtenerTodosLosPedidos(){
        return pedidoRepository.findAll();
    }
}
