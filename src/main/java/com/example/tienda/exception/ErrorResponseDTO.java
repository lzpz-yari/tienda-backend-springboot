package com.example.tienda.exception;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Forma ÚNICA en la que la API responde cuando algo sale mal.
 * Los campos que vengan en null no se envían en el JSON.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Estructura estándar de respuesta ante errores")
public record ErrorResponseDTO(

        @Schema(example = "2026-11-16T08:15:30.120")
        LocalDateTime timestamp,

        @Schema(example = "404")
        int status,

        @Schema(example = "Recurso no encontrado")
        String error,

        @Schema(example = "No se encontró el producto con ID: 999")
        String mensaje,

        @Schema(example = "/api/productos/999")
        String path,

        @Schema(example = "{\"nombre\": \"El nombre del producto es obligatorio\"}")
        Map<String, String> detalles
) {}