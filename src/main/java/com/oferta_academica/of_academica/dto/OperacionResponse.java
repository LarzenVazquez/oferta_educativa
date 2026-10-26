package com.oferta_academica.of_academica.dto;

// Respuesta de las acciones (registrar, editar, enviar, aprobar...): mensaje + registro resultante
public record OperacionResponse<T>(String mensaje, T datos) {
}
