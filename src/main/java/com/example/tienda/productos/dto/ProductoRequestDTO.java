package com.example.tienda.productos.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * DTO de ENTRADA: los datos que el cliente (Vue o Swagger) ENVÍA para crear/editar un producto.
 * Aquí van las validaciones. Si no se cumplen, la API responde 400 automáticamente.
 */
public record ProductoRequestDTO(

        @NotBlank(message = "El nombre del producto es obligatorio y no puede estar vacío")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        @Schema(example = "Teclado Mecánico RGB")
        String nombre,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser mayor a 0")
        @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 8 enteros y 2 decimales")
        @Schema(example = "1250.50")
        BigDecimal precio,

        @NotNull(message = "El stock es obligatorio")
        @PositiveOrZero(message = "El stock no puede ser negativo")
        @Schema(example = "15")
        Integer stock
) {}