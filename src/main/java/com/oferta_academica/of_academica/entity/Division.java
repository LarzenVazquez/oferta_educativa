package com.of.oferta_educativa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "divisiones")
@Data
public class Division {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 150)
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false, length = 1000)
    private String descripcion;

    @NotNull(message = "El estatus es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estatus estatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAprobacion estadoAprobacion = EstadoAprobacion.BORRADOR;

    @Column(nullable = false)
    private boolean publicado = false;

    @Column(length = 500)
    private String motivoRechazo;
}
