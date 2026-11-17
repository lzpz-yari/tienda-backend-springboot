package com.example.tienda.ventas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tienda.exception.ErrorResponseDTO;
import com.example.tienda.ventas.dto.VentaRequestDTO;
import com.example.tienda.ventas.dto.VentaResponseDTO;
import com.example.tienda.ventas.service.VentaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Puerta de entrada HTTP del módulo VENTAS.
 */
@RestController
@RequestMapping("/api/ventas")
@Tag(name = "Ventas", description = "Registro, consulta y cancelación de ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    // GET /api/ventas
    @Operation(summary = "Listar todas las ventas",
            description = "Devuelve las ventas (más recientes primero) con sus productos.")
    @ApiResponse(responseCode = "200", description = "Lista recuperada correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = VentaResponseDTO.class))))
    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listarVentas() {
        return ResponseEntity.ok(ventaService.findAll());
    }

    // GET /api/ventas/{id}
    @Operation(summary = "Obtener venta por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venta encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe una venta con ese ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> obtenerVentaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.obtenerPorId(id));
    }

    // POST /api/ventas
    @Operation(summary = "Registrar una venta",
            description = "Valida stock, lo descuenta y calcula el total automáticamente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Venta registrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (sin cliente, sin productos, cantidad 0...)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Algún producto de la venta no existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Stock insuficiente",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<VentaResponseDTO> registrarVenta(@Valid @RequestBody VentaRequestDTO request) {
        return new ResponseEntity<>(ventaService.registrarVenta(request), HttpStatus.CREATED);
    }

    // DELETE /api/ventas/{id}
    @Operation(summary = "Cancelar (eliminar) una venta",
            description = "Elimina la venta y devuelve las piezas al stock de cada producto.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Venta cancelada"),
            @ApiResponse(responseCode = "404", description = "No existe una venta con ese ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarVenta(@PathVariable Long id) {
        ventaService.cancelarVenta(id);
        return ResponseEntity.noContent().build();
    }
}