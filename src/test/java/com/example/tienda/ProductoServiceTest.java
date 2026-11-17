package com.example.tienda;

import com.example.tienda.exception.ReglaNegocioException;
import com.example.tienda.exception.ResourceNotAvailable;
import com.example.tienda.productos.dto.ProductoRequestDTO;
import com.example.tienda.productos.dto.ProductoResponseDTO;
import com.example.tienda.productos.model.Producto;
import com.example.tienda.productos.repository.ProductoRepository;
import com.example.tienda.productos.service.ProductoService;
import com.example.tienda.ventas.repository.DetalleVentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas UNITARIAS de la lógica de productos.
 * No usan base de datos: los repositorios se "simulan" con Mockito (@Mock).
 * Se ejecutan con:  .\mvnw.cmd test
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void saveProducto_devuelveElProductoConId() {
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto p = inv.getArgument(0);
            p.setId(1L);                       // simulamos que la BD le asignó el id 1
            return p;
        });

        ProductoResponseDTO respuesta = productoService.saveProducto(
                new ProductoRequestDTO("Teclado", new BigDecimal("100.00"), 10));

        assertEquals(1L, respuesta.id());
        assertEquals("Teclado", respuesta.nombre());
    }

    @Test
    void obtenerPorId_inexistente_lanzaError() {
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotAvailable.class, () -> productoService.obtenerPorId(999L));
    }

    @Test
    void deleteProducto_conVentas_noSePermite() {
        Producto p = new Producto();
        p.setId(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(p));
        when(detalleVentaRepository.existsByProductoId(1L)).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> productoService.deleteProducto(1L));
        verify(productoRepository, never()).delete(any());
    }
}