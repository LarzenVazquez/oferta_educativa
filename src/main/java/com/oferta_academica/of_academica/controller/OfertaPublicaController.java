package com.oferta_academica.of_academica.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oferta_academica.of_academica.dto.CarreraDetallePublicaResponse;
import com.oferta_academica.of_academica.dto.OfertaPublicaDivisionResponse;
import com.oferta_academica.of_academica.service.OfertaService;

// Página web pública: solo lo aprobado por el Secretario
@RestController
@RequestMapping("/api/public/oferta")
public class OfertaPublicaController {

    private final OfertaService ofertaService;

    public OfertaPublicaController(OfertaService ofertaService) {
        this.ofertaService = ofertaService;
    }

    // HU14: divisiones con sus carreras
    @GetMapping
    public List<OfertaPublicaDivisionResponse> listar() {
        return ofertaService.listarPublica();
    }

    // HU15: detalle de una carrera
    @GetMapping("/carrera/{id}")
    public CarreraDetallePublicaResponse detalleCarrera(@PathVariable Long id) {
        return ofertaService.detalleCarreraPublica(id);
    }
}
