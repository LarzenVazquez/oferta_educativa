package com.oferta_academica.of_academica.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oferta_academica.of_academica.dto.OfertaItemResponse;
import com.oferta_academica.of_academica.service.OfertaService;

// Rol: Administrador - HU07
@RestController
@RequestMapping("/api/admin/oferta")
public class OfertaController {

    private final OfertaService ofertaService;

    public OfertaController(OfertaService ofertaService) {
        this.ofertaService = ofertaService;
    }

    @GetMapping
    public List<OfertaItemResponse> listar(@RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long divisionId) {
        return ofertaService.listarParaAdministrador(nombre, divisionId);
    }
}
