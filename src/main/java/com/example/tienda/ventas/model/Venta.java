package com.example.tienda.ventas.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Tabla "ventas": el ENCABEZADO de una venta (quién compró, cuándo y cuánto pagó).
 * Una venta tiene VARIOS renglones (DetalleVenta): relación uno-a-muchos.
 */
@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, length = 100)
    private String cliente;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    // mappedBy = "venta" -> el campo "venta" de DetalleVenta es el dueño de la relación.
    // cascade ALL -> al guardar/borrar la venta, también se guardan/borran sus detalles.
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();

    public Venta() {}

    // Agrega un renglón y le dice a ese renglón a qué venta pertenece
    public void agregarDetalle(DetalleVenta detalle) {
        detalle.setVenta(this);
        this.detalles.add(detalle);
    }

    // ----- Getters y Setters -----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }
}