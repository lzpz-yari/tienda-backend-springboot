package com.example.tienda.productos.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * ENTIDAD = representa una TABLA de la base de datos.
 * Cada objeto Producto es una FILA de la tabla "productos".
 */
@Entity
@Table(name = "productos")
public class Producto {

    @Id                                                   // llave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // la BD genera el id (BIGSERIAL)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;

    public Producto() {}

    // ----- Getters y Setters -----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}