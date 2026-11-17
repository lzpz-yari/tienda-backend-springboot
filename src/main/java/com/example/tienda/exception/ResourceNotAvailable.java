package com.example.tienda.exception;

/**
 * Se lanza cuando se busca algo (producto o venta) que NO existe.
 * El GlobalExceptionHandler la convierte en una respuesta HTTP 404.
 */
public class ResourceNotAvailable extends RuntimeException {
    public ResourceNotAvailable(String message) {
        super(message);
    }
}