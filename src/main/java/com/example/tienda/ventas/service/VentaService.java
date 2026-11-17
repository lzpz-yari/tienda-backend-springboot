package com.example.tienda.ventas.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tienda.exception.ReglaNegocioException;
import com.example.tienda.exception.ResourceNotAvailable;
import com.example.tienda.productos.model.Producto;
import com.example.tienda.productos.repository.ProductoRepository;
import com.example.tienda.ventas.dto.DetalleVentaRequestDTO;
import com.example.tienda.ventas.dto.DetalleVentaResponseDTO;
import com.example.tienda.ventas.dto.VentaRequestDTO;
import com.example.tienda.ventas.dto.VentaResponseDTO;
import com.example.tienda.ventas.model.DetalleVenta;
import com.example.tienda.ventas.model.Venta;
import com.example.tienda.ventas.repository.VentaRepository;

/**
 * Lógica del módulo VENTAS.
 * Reglas:
 *  1. Al registrar una venta se valida que haya stock suficiente y se DESCUENTA.
 *  2. El precio y el total los calcula el backend (no se confía en lo que mande el cliente).
 *  3. Al cancelar (eliminar) una venta, el stock se DEVUELVE a los productos.
 * @Transactional: si algo falla a la mitad, se deshace TODO (no queda stock descontado a medias).
 */
@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<VentaResponseDTO> findAll() {
        return ventaRepository.findAllByOrderByFechaDesc()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public VentaResponseDTO obtenerPorId(Long id) {
        return toResponseDTO(buscarEntidadPorId(id));
    }

    @Transactional
    public VentaResponseDTO registrarVenta(VentaRequestDTO request) {
        Venta venta = new Venta();
        venta.setCliente(request.cliente());
        venta.setFecha(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;

        for (DetalleVentaRequestDTO item : request.detalles()) {
            // 1) ¿Existe el producto?
            Producto producto = productoRepository.findById(item.productoId())
                    .orElseThrow(() -> new ResourceNotAvailable(
                            "No se encontró el producto con ID: " + item.productoId()));

            // 2) ¿Hay suficiente stock?
            if (producto.getStock() < item.cantidad()) {
                throw new ReglaNegocioException("Stock insuficiente para '" + producto.getNombre()
                        + "'. Disponible: " + producto.getStock() + ", solicitado: " + item.cantidad());
            }

            // 3) Descontar stock (Hibernate guarda el cambio al terminar la transacción)
            producto.setStock(producto.getStock() - item.cantidad());

            // 4) Crear el renglón con precio y subtotal calculados aquí
            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.cantidad()));

            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setCantidad(item.cantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(subtotal);
            venta.agregarDetalle(detalle);

            total = total.add(subtotal);
        }

        venta.setTotal(total);
        Venta ventaGuardada = ventaRepository.save(venta);   // guarda venta + detalles (cascade)
        return toResponseDTO(ventaGuardada);
    }

    @Transactional
    public void cancelarVenta(Long id) {
        Venta venta = buscarEntidadPorId(id);

        // Devolver al inventario las piezas de cada renglón
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
        }
        ventaRepository.delete(venta);   // borra venta + detalles (cascade)
    }

    // ---------------- Métodos auxiliares privados ----------------

    private Venta buscarEntidadPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotAvailable("No se encontró la venta con ID: " + id));
    }

    private VentaResponseDTO toResponseDTO(Venta venta) {
        List<DetalleVentaResponseDTO> detalles = venta.getDetalles()
                .stream()
                .map(d -> new DetalleVentaResponseDTO(
                        d.getProducto().getId(),
                        d.getProducto().getNombre(),
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                        d.getSubtotal()))
                .toList();

        return new VentaResponseDTO(venta.getId(), venta.getFecha(), venta.getCliente(), venta.getTotal(), detalles);
    }
}