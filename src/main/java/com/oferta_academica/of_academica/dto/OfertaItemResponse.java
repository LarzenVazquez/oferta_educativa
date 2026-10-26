package com.oferta_academica.of_academica.dto;

import com.oferta_academica.of_academica.entity.Carrera;
import com.oferta_academica.of_academica.entity.Division;
import com.oferta_academica.of_academica.entity.EstadoAprobacion;
import com.oferta_academica.of_academica.entity.Estatus;

// Fila del listado de oferta académica del Administrador (HU07)
public record OfertaItemResponse(
        TipoRegistro tipo,
        Long id,
        String nombre,
        String division,
        Estatus estatus,
        EstadoAprobacion estadoAprobacion,
        String motivoRechazo) {

    public static OfertaItemResponse de(Division d) {
        return new OfertaItemResponse(TipoRegistro.DIVISION, d.getId(), d.getNombre(), d.getNombre(),
                d.getEstatus(), d.getEstadoAprobacion(), d.getMotivoRechazo());
    }

    public static OfertaItemResponse de(Carrera c) {
        return new OfertaItemResponse(TipoRegistro.CARRERA, c.getId(), c.getNombre(), c.getDivision().getNombre(),
                c.getEstatus(), c.getEstadoAprobacion(), c.getMotivoRechazo());
    }
}
