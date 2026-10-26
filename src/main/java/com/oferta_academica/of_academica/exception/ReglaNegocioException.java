package com.oferta_academica.of_academica.exception;

// Operación no permitida por el estado actual del registro. Se responde con 409 Conflict
public class ReglaNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
