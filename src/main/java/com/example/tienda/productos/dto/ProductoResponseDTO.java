package com.example.tienda.productos.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de SALIDA: lo que la API DEVUELVE al cliente cuando pide un producto.
 */
public record ProductoResponseDTO(

        @Schema(example = "1")
        Long id,

        @Schema(example = "Teclado Mecánico RGB")
        String nombre,

        @Schema(example = "1250.50")
        BigDecimal precio,

        @Schema(example = "15")
        Integer stock
) {}