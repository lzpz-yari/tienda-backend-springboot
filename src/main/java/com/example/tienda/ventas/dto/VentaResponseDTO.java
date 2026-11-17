package com.example.tienda.ventas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/** La venta completa tal como la DEVUELVE la API (encabezado + renglones). */
public record VentaResponseDTO(

        @Schema(example = "1")
        Long id,

        @Schema(example = "2026-11-16T10:30:00")
        LocalDateTime fecha,

        @Schema(example = "Juan Pérez")
        String cliente,

        @Schema(example = "2501.00")
        BigDecimal total,

        List<DetalleVentaResponseDTO> detalles
) {}