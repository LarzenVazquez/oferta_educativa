package com.oferta_academica.of_academica.dto;

import com.oferta_academica.of_academica.entity.Estatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// HU01 / HU02: nombre, descripción y estatus son obligatorios
public record DivisionRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
        String nombre,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
        String descripcion,

        @NotNull(message = "El estatus es obligatorio")
        Estatus estatus) {
}
