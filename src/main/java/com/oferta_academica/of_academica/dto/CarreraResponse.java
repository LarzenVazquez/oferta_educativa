package com.oferta_academica.of_academica.dto;

import com.oferta_academica.of_academica.entity.Carrera;
import com.oferta_academica.of_academica.entity.EstadoAprobacion;
import com.oferta_academica.of_academica.entity.Estatus;
import com.oferta_academica.of_academica.entity.Modalidad;

public record CarreraResponse(
        Long id,
        String nombre,
        String descripcion,
        Integer duracionCuatrimestres,
        Modalidad modalidad,
        Long divisionId,
        String divisionNombre,
        Estatus estatus,
        EstadoAprobacion estadoAprobacion,
        boolean publicado,
        String motivoRechazo) {

    public static CarreraResponse de(Carrera c) {
        return new CarreraResponse(c.getId(), c.getNombre(), c.getDescripcion(), c.getDuracionCuatrimestres(),
                c.getModalidad(), c.getDivision().getId(), c.getDivision().getNombre(), c.getEstatus(),
                c.getEstadoAprobacion(), c.isPublicado(), c.getMotivoRechazo());
    }
}
