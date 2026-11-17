package com.example.tienda.ventas.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * Lo que se ENVÍA para registrar una venta.
 * Ejemplo JSON:
 * {
 *   "cliente": "Juan Pérez",
 *   "detalles": [ { "productoId": 1, "cantidad": 2 }, { "productoId": 3, "cantidad": 1 } ]
 * }
 */
public record VentaRequestDTO(

        @NotBlank(message = "El nombre del cliente es obligatorio")
        @Size(max = 100, message = "El nombre del cliente debe tener máximo 100 caracteres")
        @Schema(example = "Juan Pérez")
        String cliente,

        @NotEmpty(message = "La venta debe tener al menos un producto")
        @Valid   // valida también cada renglón de la lista
        List<DetalleVentaRequestDTO> detalles
) {}