<<<<<<< HEAD:src/main/java/com/of/oferta_educativa/entity/Carrera.java
package com.of.oferta_educativa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Carrera: programa de estudios. Siempre pertenece a una division
@Entity
@Table(name = "carreras")
@Data 
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 150)
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false, length = 1000)
    private String descripcion;

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser de al menos 1 cuatrimestre")
    @Column(nullable = false)
    private Integer duracionCuatrimestres;

    @NotNull(message = "La modalidad es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Modalidad modalidad;

    @NotNull(message = "La división es obligatoria")
    @ManyToOne
    @JoinColumn(name = "division_id", nullable = false)
    private Division division;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estatus estatus = Estatus.ACTIVO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAprobacion estadoAprobacion = EstadoAprobacion.BORRADOR;

    @Column(nullable = false)
    private boolean publicado = false;

    @Column(length = 500)
    private String motivoRechazo;
}
=======
package com.oferta_academica.of_academica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Carrera: programa de estudios. Siempre pertenece a una division
@Entity
@Table(name = "carreras")
@Data
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 150)
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false, length = 1000)
    private String descripcion;

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser de al menos 1 cuatrimestre")
    @Column(nullable = false)
    private Integer duracionCuatrimestres;

    @NotNull(message = "La modalidad es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Modalidad modalidad;

    @NotNull(message = "La división es obligatoria")
    @ManyToOne
    @JoinColumn(name = "division_id", nullable = false)
    private Division division;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estatus estatus = Estatus.ACTIVO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAprobacion estadoAprobacion = EstadoAprobacion.BORRADOR;

    @Column(nullable = false)
    private boolean publicado = false;

    @Column(length = 500)
    private String motivoRechazo;
}
>>>>>>> upstream/main:src/main/java/com/oferta_academica/of_academica/entity/Carrera.java
