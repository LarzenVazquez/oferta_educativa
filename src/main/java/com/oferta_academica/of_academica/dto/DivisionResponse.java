package com.oferta_academica.of_academica.dto;

import com.oferta_academica.of_academica.entity.Division;
import com.oferta_academica.of_academica.entity.EstadoAprobacion;
import com.oferta_academica.of_academica.entity.Estatus;

public record DivisionResponse(
        Long id,
        String nombre,
        String descripcion,
        Estatus estatus,
        EstadoAprobacion estadoAprobacion,
        boolean publicado,
        String motivoRechazo) {

    public static DivisionResponse de(Division d) {
        return new DivisionResponse(d.getId(), d.getNombre(), d.getDescripcion(), d.getEstatus(),
                d.getEstadoAprobacion(), d.isPublicado(), d.getMotivoRechazo());
    }
}
