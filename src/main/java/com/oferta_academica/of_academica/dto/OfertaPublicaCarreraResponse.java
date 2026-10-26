package com.oferta_academica.of_academica.dto;

import com.oferta_academica.of_academica.entity.Carrera;
import com.oferta_academica.of_academica.entity.Modalidad;

public record OfertaPublicaCarreraResponse(
        Long id,
        String nombre,
        String descripcion,
        Integer duracionCuatrimestres,
        Modalidad modalidad) {

    public static OfertaPublicaCarreraResponse de(Carrera c) {
        return new OfertaPublicaCarreraResponse(c.getId(), c.getNombre(), c.getDescripcion(),
                c.getDuracionCuatrimestres(), c.getModalidad());
    }
}
