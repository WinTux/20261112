package com.pepito.ProductoService.Services;
import com.pepito.ProductoService.Models.Producto;
import com.pepito.ProductoService.Repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;
    public List<Producto> findAll() {
        log.info("Consultando todos los productos");
        List<Producto> productos = productoRepository.findAll();
        log.info("Consulta de productos finalizada. cantidad={}", productos.size());
        return productos;
    }
    public Producto findById(Long id) {
        log.info("Buscando producto por id. id={}", id);
        return productoRepository.findById(id)
                .map(producto -> {
                    log.info("Producto encontrado correctamente. id={}", id);
                    log.debug("Detalle del producto encontrado. producto={}", producto);
                    return producto;
                })
                .orElseThrow(() -> {
                    log.error("No existe el producto con el id={}", id);
                    return new RuntimeException("No existe el producto con el id: " + id);
                });
    }
    public Producto guardar(Producto producto) {
        log.info("Guardando producto");
        log.debug("Detalle del producto a guardar. producto={}", producto);
        Producto productoGuardado = productoRepository.save(producto);
        log.info("Producto guardado correctamente. id={}", productoGuardado.getId());
        return productoGuardado;
    }
}
