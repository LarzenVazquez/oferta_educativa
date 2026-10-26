package com.oferta_academica.of_academica.dto;

import com.oferta_academica.of_academica.entity.Carrera;
import com.oferta_academica.of_academica.entity.Division;
import com.oferta_academica.of_academica.entity.EstadoAprobacion;
import com.oferta_academica.of_academica.entity.RegistroAprobable;

// Solicitud en la bandeja del Secretario (HU10)
public record SolicitudResponse(
        TipoRegistro tipo,
        Long id,
        String nombre,
        String descripcion,
        String division,
        EstadoAprobacion estadoAprobacion,
        String tipoSolicitud) {

    public static SolicitudResponse de(Division d) {
        return new SolicitudResponse(TipoRegistro.DIVISION, d.getId(), d.getNombre(), d.getDescripcion(),
                d.getNombre(), d.getEstadoAprobacion(), tipoSolicitud(d));
    }

    public static SolicitudResponse de(Carrera c) {
        return new SolicitudResponse(TipoRegistro.CARRERA, c.getId(), c.getNombre(), c.getDescripcion(),
                c.getDivision().getNombre(), c.getEstadoAprobacion(), tipoSolicitud(c));
    }

    private static String tipoSolicitud(RegistroAprobable r) {
        return r.getEstadoAprobacion() == EstadoAprobacion.PENDIENTE_DESACTIVACION ? "Desactivación" : "Publicación";
    }
}
