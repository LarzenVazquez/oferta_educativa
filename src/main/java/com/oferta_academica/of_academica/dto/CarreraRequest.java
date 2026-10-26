package com.oferta_academica.of_academica.dto;

import com.oferta_academica.of_academica.entity.Modalidad;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CarreraRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
        String nombre,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
        String descripcion,

        @NotNull(message = "La duración es obligatoria")
        @Min(value = 1, message = "La duración debe ser de al menos 1 cuatrimestre")
        Integer duracionCuatrimestres,

        @NotNull(message = "La modalidad es obligatoria")
        Modalidad modalidad,

        @NotNull(message = "La división es obligatoria")
        Long divisionId) {
}
