package com.example.tienda.ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tienda.ventas.model.DetalleVenta;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    // ¿Existe algún renglón de venta con este producto?
    // SQL equivalente: SELECT COUNT(*) > 0 FROM detalle_venta WHERE producto_id = ?
    boolean existsByProductoId(Long productoId);
}