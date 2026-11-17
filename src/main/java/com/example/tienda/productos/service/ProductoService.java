package com.example.tienda.productos.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tienda.exception.ReglaNegocioException;
import com.example.tienda.exception.ResourceNotAvailable;
import com.example.tienda.productos.dto.ProductoRequestDTO;
import com.example.tienda.productos.dto.ProductoResponseDTO;
import com.example.tienda.productos.model.Producto;
import com.example.tienda.productos.repository.ProductoRepository;
import com.example.tienda.ventas.repository.DetalleVentaRepository;

/**
 * SERVICIO = aquí va la LÓGICA del negocio.
 * El controlador le pide cosas al servicio, y el servicio usa el repositorio.
 */
@Service
public class ProductoService {

        private final ProductoRepository productoRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    // Inyección de dependencias por constructor: Spring nos "pasa" los repositorios
    public ProductoService(ProductoRepository productoRepository,
                           DetalleVentaRepository detalleVentaRepository) {
        this.productoRepository = productoRepository;
        this.detalleVentaRepository = detalleVentaRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> findAll() {
        return productoRepository.findAllByOrderByIdAsc()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long id) {
        return toResponseDTO(buscarEntidadPorId(id));
    }

    @Transactional
    public ProductoResponseDTO saveProducto(ProductoRequestDTO requestDTO) {
        Producto nuevoProducto = toEntity(requestDTO);
        Producto productoGuardado = productoRepository.save(nuevoProducto);
        return toResponseDTO(productoGuardado);
    }

    @Transactional
    public ProductoResponseDTO updateProducto(Long id, ProductoRequestDTO requestDTO) {
        Producto productoExistente = buscarEntidadPorId(id);

        productoExistente.setNombre(requestDTO.nombre());
        productoExistente.setPrecio(requestDTO.precio());
        productoExistente.setStock(requestDTO.stock());

        return toResponseDTO(productoRepository.save(productoExistente));
    }

        @Transactional
    public void deleteProducto(Long id) {
        Producto productoExistente = buscarEntidadPorId(id);

        // REGLA DE NEGOCIO: no se borra un producto que ya se vendió (se perdería el historial)
        if (detalleVentaRepository.existsByProductoId(id)) {
            throw new ReglaNegocioException(
                    "No se puede eliminar el producto con ID " + id + " porque tiene ventas registradas");
        }
        productoRepository.delete(productoExistente);
    }

    // ---------------- Métodos auxiliares privados ----------------

    private Producto buscarEntidadPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotAvailable("No se encontró el producto con ID: " + id));
    }

    // DTO -> Entidad
    private Producto toEntity(ProductoRequestDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.nombre());
        producto.setPrecio(dto.precio());
        producto.setStock(dto.stock());
        return producto;
    }

    // Entidad -> DTO
    private ProductoResponseDTO toResponseDTO(Producto producto) {
        return new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock()
        );
    }
}