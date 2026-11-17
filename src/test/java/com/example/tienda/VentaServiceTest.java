package com.example.tienda;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.tienda.exception.ReglaNegocioException;
import com.example.tienda.exception.ResourceNotAvailable;
import com.example.tienda.productos.model.Producto;
import com.example.tienda.productos.repository.ProductoRepository;
import com.example.tienda.ventas.dto.DetalleVentaRequestDTO;
import com.example.tienda.ventas.dto.VentaRequestDTO;
import com.example.tienda.ventas.dto.VentaResponseDTO;
import com.example.tienda.ventas.model.DetalleVenta;
import com.example.tienda.ventas.model.Venta;
import com.example.tienda.ventas.repository.VentaRepository;
import com.example.tienda.ventas.service.VentaService;

/**
 * Pruebas UNITARIAS de la lógica de ventas.
 * No usan base de datos: los repositorios se "simulan" con Mockito (@Mock).
 * Se ejecutan con:  .\mvnw.cmd test
 */
@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private VentaService ventaService;

    private Producto crearProducto(Long id, String nombre, String precio, int stock) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setPrecio(new BigDecimal(precio));
        p.setStock(stock);
        return p;
    }

    @Test
    void registrarVenta_descuentaStockYCalculaTotal() {
        Producto teclado = crearProducto(1L, "Teclado", "100.00", 10);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(teclado));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));

        VentaRequestDTO request = new VentaRequestDTO("Juan",
                List.of(new DetalleVentaRequestDTO(1L, 3)));

        VentaResponseDTO respuesta = ventaService.registrarVenta(request);

        assertEquals(7, teclado.getStock());                              // 10 - 3
        assertEquals(new BigDecimal("300.00"), respuesta.total());        // 3 x 100.00
        assertEquals(1, respuesta.detalles().size());
    }

    @Test
    void registrarVenta_conStockInsuficiente_lanzaError() {
        Producto mouse = crearProducto(2L, "Mouse", "50.00", 1);
        when(productoRepository.findById(2L)).thenReturn(Optional.of(mouse));

        VentaRequestDTO request = new VentaRequestDTO("Ana",
                List.of(new DetalleVentaRequestDTO(2L, 5)));

        assertThrows(ReglaNegocioException.class, () -> ventaService.registrarVenta(request));
        assertEquals(1, mouse.getStock());                       // el stock no cambió
        verify(ventaRepository, never()).save(any());            // no se guardó nada
    }

    @Test
    void registrarVenta_conProductoInexistente_lanzaError() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        VentaRequestDTO request = new VentaRequestDTO("Luis",
                List.of(new DetalleVentaRequestDTO(99L, 1)));

        assertThrows(ResourceNotAvailable.class, () -> ventaService.registrarVenta(request));
    }

    @Test
    void cancelarVenta_devuelveElStock() {
        Producto monitor = crearProducto(3L, "Monitor", "3000.00", 4);

        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(monitor);
        detalle.setCantidad(2);

        Venta venta = new Venta();
        venta.setId(1L);
        venta.agregarDetalle(detalle);

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        ventaService.cancelarVenta(1L);

        assertEquals(6, monitor.getStock());     // 4 + 2 devueltos
        verify(ventaRepository).delete(venta);
    }
}