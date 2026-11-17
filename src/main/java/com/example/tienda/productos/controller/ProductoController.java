package com.example.tienda.productos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tienda.exception.ErrorResponseDTO;
import com.example.tienda.productos.dto.ProductoRequestDTO;
import com.example.tienda.productos.dto.ProductoResponseDTO;
import com.example.tienda.productos.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * CONTROLADOR = la "puerta de entrada" HTTP del módulo PRODUCTOS.
 * Cada método es un ENDPOINT (una URL + un verbo HTTP).
 * Las anotaciones @Operation / @ApiResponse solo sirven para que Swagger se vea documentado.
 */
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "CRUD del catálogo de productos (inventario)")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // GET /api/productos  -> lista todos
    @Operation(summary = "Listar todos los productos",
            description = "Devuelve todos los productos registrados. Si no hay, devuelve una lista vacía [].")
    @ApiResponse(responseCode = "200", description = "Lista recuperada correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductoResponseDTO.class))))
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listarProductos() {
        return ResponseEntity.ok(productoService.findAll());
    }

    // GET /api/productos/{id}  -> uno por id
    @Operation(summary = "Obtener producto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un producto con ese ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerProductoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    // POST /api/productos  -> crea uno nuevo
    @Operation(summary = "Registrar un nuevo producto",
            description = "Valida los datos y guarda el producto en PostgreSQL.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (nombre vacío, precio negativo, etc.)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crearProducto(@Valid @RequestBody ProductoRequestDTO producto) {
        ProductoResponseDTO nuevoProducto = productoService.saveProducto(producto);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    // PUT /api/productos/{id}  -> actualiza
    @Operation(summary = "Actualizar un producto existente",
            description = "Reemplaza nombre, precio y stock del producto indicado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe un producto con ese ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(@PathVariable Long id,
                                                                  @Valid @RequestBody ProductoRequestDTO requestDTO) {
        return ResponseEntity.ok(productoService.updateProducto(id, requestDTO));
    }

    // DELETE /api/productos/{id}  -> elimina
    @Operation(summary = "Eliminar un producto",
            description = "Elimina el producto. No se permite si el producto ya tiene ventas.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Eliminado (sin contenido en la respuesta)"),
            @ApiResponse(responseCode = "404", description = "No existe un producto con ese ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "El producto tiene ventas registradas",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }
}