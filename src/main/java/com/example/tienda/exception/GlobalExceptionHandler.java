package com.example.tienda.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * "Atrapa" los errores de TODOS los controladores y los convierte
 * en una respuesta JSON clara (ErrorResponseDTO) con su código HTTP.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1. Validaciones (@NotBlank, @Positive, etc.) -> 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> manejarValidaciones(MethodArgumentNotValidException ex,
                                                                HttpServletRequest request) {
        Map<String, String> erroresCampos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                erroresCampos.put(error.getField(), error.getDefaultMessage()));

        return construir(HttpStatus.BAD_REQUEST, "Error de validación", null, request, erroresCampos);
    }

    // 2. JSON mal escrito o tipo de dato incorrecto (ej. "precio": "abc") -> 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> manejarJsonInvalido(HttpMessageNotReadableException ex,
                                                                HttpServletRequest request) {
        return construir(HttpStatus.BAD_REQUEST, "Cuerpo JSON inválido o mal formado",
                ex.getMostSpecificCause().getMessage(), request, null);
    }

    // 3. ID con formato incorrecto en la URL (ej. /api/productos/abc) -> 400
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> manejarTipoIncorrecto(MethodArgumentTypeMismatchException ex,
                                                                  HttpServletRequest request) {
        return construir(HttpStatus.BAD_REQUEST, "Parámetro inválido",
                "El valor '" + ex.getValue() + "' no es válido para '" + ex.getName() + "'", request, null);
    }

    // 4. No existe el producto / venta -> 404
    @ExceptionHandler(ResourceNotAvailable.class)
    public ResponseEntity<ErrorResponseDTO> manejarNoEncontrado(ResourceNotAvailable ex,
                                                                HttpServletRequest request) {
        return construir(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage(), request, null);
    }

    // 5. Regla de negocio (stock insuficiente, producto con ventas) -> 409
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponseDTO> manejarReglaNegocio(ReglaNegocioException ex,
                                                                HttpServletRequest request) {
        return construir(HttpStatus.CONFLICT, "Operación no permitida", ex.getMessage(), request, null);
    }

    // 6. Cualquier otro error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> manejarGenerico(Exception ex, HttpServletRequest request) {
        // Errores "normales" de Spring (ruta que no existe = 404, método no permitido = 405...)
        if (ex instanceof ErrorResponse errorDeSpring) {
            HttpStatus status = HttpStatus.valueOf(errorDeSpring.getStatusCode().value());
            return construir(status, status.getReasonPhrase(), ex.getMessage(), request, null);
        }
        // Error inesperado -> 500 (se guarda en el log, no se muestra el detalle al cliente)
        log.error("Error no controlado en {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", null, request, null);
    }

    // Método auxiliar para no repetir código
    private ResponseEntity<ErrorResponseDTO> construir(HttpStatus status, String error, String mensaje,
                                                       HttpServletRequest request, Map<String, String> detalles) {
        ErrorResponseDTO respuesta = new ErrorResponseDTO(
                LocalDateTime.now(), status.value(), error, mensaje, request.getRequestURI(), detalles);
        return ResponseEntity.status(status).body(respuesta);
    }
}