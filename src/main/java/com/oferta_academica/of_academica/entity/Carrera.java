package com.oferta_academica.of_academica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Carrera: programa de estudios. Siempre pertenece a una división
@Entity
@Table(name = "carreras")
public class Carrera extends RegistroAprobable {

    @Column(nullable = false)
    private Integer duracionCuatrimestres;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Modalidad modalidad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "division_id", nullable = false)
    private Division division;

    protected Carrera() {
    }

    public Carrera(String nombre, String descripcion, Integer duracionCuatrimestres, Modalidad modalidad,
            Division division) {
        super(nombre, descripcion, Estatus.ACTIVO);
        this.duracionCuatrimestres = duracionCuatrimestres;
        this.modalidad = modalidad;
        this.division = division;
    }

    public void actualizarDatos(String nombre, String descripcion, Integer duracionCuatrimestres,
            Modalidad modalidad, Division division) {
        actualizarDatosBase(nombre, descripcion, getEstatus());
        this.duracionCuatrimestres = duracionCuatrimestres;
        this.modalidad = modalidad;
        this.division = division;
    }

    @Override
    protected String etiqueta() {
        return "La carrera";
    }

    public Integer getDuracionCuatrimestres() {
        return duracionCuatrimestres;
    }

    public Modalidad getModalidad() {
        return modalidad;
    }

    public Division getDivision() {
        return division;
    }
}
