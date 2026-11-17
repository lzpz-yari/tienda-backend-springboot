package com.example.tienda.ventas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Un renglón que el cliente envía al registrar una venta: qué producto y cuántas piezas.
 * (El precio NO se envía: el backend lo toma del producto para que nadie lo altere).
 */
public record DetalleVentaRequestDTO(

        @NotNull(message = "El ID del producto es obligatorio")
        @Schema(example = "1")
        Long productoId,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a 0")
        @Schema(example = "2")
        Integer cantidad
) {}