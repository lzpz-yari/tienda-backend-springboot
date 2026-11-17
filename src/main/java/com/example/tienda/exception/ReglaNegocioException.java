package com.example.tienda.exception;

/**
 * Se lanza cuando los datos son correctos pero la operación NO se permite
 * por una regla del negocio. Ejemplos:
 *  - Vender más piezas de las que hay en stock.
 *  - Eliminar un producto que ya aparece en ventas.
 * El GlobalExceptionHandler la convierte en una respuesta HTTP 409 (Conflict).
 */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String message) {
        super(message);
    }
}