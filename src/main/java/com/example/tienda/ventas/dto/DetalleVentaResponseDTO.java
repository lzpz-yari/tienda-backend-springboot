package com.example.tienda.ventas.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

/** Un renglón de la venta tal como lo DEVUELVE la API. */
public record DetalleVentaResponseDTO(

        @Schema(example = "1")
        Long productoId,

        @Schema(example = "Teclado Mecánico RGB")
        String nombreProducto,

        @Schema(example = "2")
        Integer cantidad,

        @Schema(example = "1250.50")
        BigDecimal precioUnitario,

        @Schema(example = "2501.00")
        BigDecimal subtotal
) {}