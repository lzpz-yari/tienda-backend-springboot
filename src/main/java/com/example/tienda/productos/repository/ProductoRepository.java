package com.example.tienda.productos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tienda.productos.model.Producto;

/**
 * REPOSITORIO = la capa que habla con la base de datos.
 * Al extender JpaRepository ya tenemos GRATIS: findAll, findById, save, delete, count...
 * Spring Data traduce esos métodos a SQL de PostgreSQL.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Spring crea el SQL a partir del NOMBRE del método:
    // SELECT * FROM productos ORDER BY id ASC
    List<Producto> findAllByOrderByIdAsc();
}