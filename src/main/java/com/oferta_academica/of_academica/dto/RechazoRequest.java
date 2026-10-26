package com.oferta_academica.of_academica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RechazoRequest(
        @NotBlank(message = "El motivo de rechazo es obligatorio")
        @Size(max = 500, message = "El motivo no puede exceder 500 caracteres")
        String motivo) {
}
