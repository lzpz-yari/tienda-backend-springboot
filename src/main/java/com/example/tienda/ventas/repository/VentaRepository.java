package com.example.tienda.ventas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tienda.ventas.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    // SELECT * FROM ventas ORDER BY fecha DESC  (las más recientes primero)
    List<Venta> findAllByOrderByFechaDesc();
}