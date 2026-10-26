package com.oferta_academica.of_academica.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.oferta_academica.of_academica.dto.DivisionRequest;
import com.oferta_academica.of_academica.dto.DivisionResponse;
import com.oferta_academica.of_academica.dto.OperacionResponse;
import com.oferta_academica.of_academica.dto.RechazoRequest;
import com.oferta_academica.of_academica.service.DivisionService;

import jakarta.validation.Valid;

// Rol: Administrador
@RestController
@RequestMapping("/api/admin/divisiones")
public class DivisionController {

    private final DivisionService divisionService;

    public DivisionController(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    @GetMapping
    public List<DivisionResponse> listar() {
        return divisionService.listar();
    }

    @GetMapping("/{id}")
    public DivisionResponse obtener(@PathVariable Long id) {
        return divisionService.obtener(id);
    }

    // HU01
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OperacionResponse<DivisionResponse> registrar(@Valid @RequestBody DivisionRequest request) {
        return new OperacionResponse<>("División registrada", divisionService.registrar(request));
    }

    // HU02
    @PutMapping("/{id}")
    public OperacionResponse<DivisionResponse> editar(@PathVariable Long id,
            @Valid @RequestBody DivisionRequest request) {
        return new OperacionResponse<>("División actualizada", divisionService.editar(id, request));
    }

    // HU08
    @PatchMapping("/{id}/enviar-aprobacion")
    public OperacionResponse<DivisionResponse> enviarAAprobacion(@PathVariable Long id) {
        return new OperacionResponse<>("División enviada a aprobación", divisionService.enviarAAprobacion(id));
    }

    // HU03
    @PatchMapping("/{id}/solicitar-desactivacion")
    public OperacionResponse<DivisionResponse> solicitarDesactivacion(@PathVariable Long id) {
        return new OperacionResponse<>("Solicitud de desactivación enviada", divisionService.solicitarDesactivacion(id));
    }

    // HU12 (Secretario Académico): rechazar solicitud con motivo obligatorio
    @PatchMapping("/{id}/rechazar")
    public OperacionResponse<DivisionResponse> rechazar(@PathVariable Long id,
            @Valid @RequestBody RechazoRequest request) {
        return new OperacionResponse<>("Solicitud rechazada", divisionService.rechazar(id, request.motivo()));
    }
}
