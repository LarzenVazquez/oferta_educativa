package com.oferta_academica.of_academica.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oferta_academica.of_academica.dto.CarreraResponse;
import com.oferta_academica.of_academica.dto.DivisionResponse;
import com.oferta_academica.of_academica.dto.OperacionResponse;
import com.oferta_academica.of_academica.dto.SolicitudResponse;
import com.oferta_academica.of_academica.service.SecretarioService;

// Rol: Secretario Académico - HU10/HU11
@RestController
@RequestMapping("/api/admin/secretario")
public class SecretarioController {

    private final SecretarioService secretarioService;

    public SecretarioController(SecretarioService secretarioService) {
        this.secretarioService = secretarioService;
    }

    // HU10
    @GetMapping("/solicitudes")
    public List<SolicitudResponse> listarSolicitudes() {
        return secretarioService.listarSolicitudes();
    }

    // HU11
    @PatchMapping("/divisiones/{id}/aprobar")
    public OperacionResponse<DivisionResponse> aprobarDivision(@PathVariable Long id) {
        return secretarioService.aprobarDivision(id);
    }

    // HU11
    @PatchMapping("/carreras/{id}/aprobar")
    public OperacionResponse<CarreraResponse> aprobarCarrera(@PathVariable Long id) {
        return secretarioService.aprobarCarrera(id);
    }
}
