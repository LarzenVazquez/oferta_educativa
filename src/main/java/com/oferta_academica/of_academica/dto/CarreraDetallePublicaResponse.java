package com.oferta_academica.of_academica.dto;

import com.oferta_academica.of_academica.entity.Carrera;
import com.oferta_academica.of_academica.entity.Modalidad;

// HU15: detalle de una carrera para la página web (descripción, duración y modalidad)
public record CarreraDetallePublicaResponse(
        Long id,
        String nombre,
        String descripcion,
        Integer duracionCuatrimestres,
        Modalidad modalidad,
        DivisionResumen division) {

    public record DivisionResumen(Long id, String nombre) {
    }

    public static CarreraDetallePublicaResponse de(Carrera c) {
        return new CarreraDetallePublicaResponse(c.getId(), c.getNombre(), c.getDescripcion(),
                c.getDuracionCuatrimestres(), c.getModalidad(),
                new DivisionResumen(c.getDivision().getId(), c.getDivision().getNombre()));
    }
}
