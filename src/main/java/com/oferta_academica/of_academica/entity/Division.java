package com.oferta_academica.of_academica.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "divisiones")
public class Division extends RegistroAprobable {

    protected Division() {
    }

    public Division(String nombre, String descripcion, Estatus estatus) {
        super(nombre, descripcion, estatus);
    }

    public void actualizarDatos(String nombre, String descripcion, Estatus estatus) {
        actualizarDatosBase(nombre, descripcion, estatus);
    }

    @Override
    protected String etiqueta() {
        return "La división";
    }
}
