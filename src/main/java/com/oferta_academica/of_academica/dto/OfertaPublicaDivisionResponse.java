package com.oferta_academica.of_academica.dto;

import java.util.List;

import com.oferta_academica.of_academica.entity.Division;

// Oferta que ve el público: divisiones visibles con sus carreras visibles
public record OfertaPublicaDivisionResponse(
        Long id,
        String nombre,
        String descripcion,
        List<OfertaPublicaCarreraResponse> carreras) {

    public static OfertaPublicaDivisionResponse de(Division d, List<OfertaPublicaCarreraResponse> carreras) {
        return new OfertaPublicaDivisionResponse(d.getId(), d.getNombre(), d.getDescripcion(), carreras);
    }
}
