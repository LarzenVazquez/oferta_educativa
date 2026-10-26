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

import com.oferta_academica.of_academica.dto.CarreraRequest;
import com.oferta_academica.of_academica.dto.CarreraResponse;
import com.oferta_academica.of_academica.dto.OperacionResponse;
import com.oferta_academica.of_academica.dto.RechazoRequest;
import com.oferta_academica.of_academica.service.CarreraService;

import jakarta.validation.Valid;

// Rol: Administrador
@RestController
@RequestMapping("/api/admin/carreras")
public class CarreraController {

    private final CarreraService carreraService;

    public CarreraController(CarreraService carreraService) {
        this.carreraService = carreraService;
    }

    @GetMapping
    public List<CarreraResponse> listar() {
        return carreraService.listar();
    }

    @GetMapping("/{id}")
    public CarreraResponse obtener(@PathVariable Long id) {
        return carreraService.obtener(id);
    }

    // HU04
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OperacionResponse<CarreraResponse> registrar(@Valid @RequestBody CarreraRequest request) {
        return new OperacionResponse<>("Carrera registrada", carreraService.registrar(request));
    }

    // HU05
    @PutMapping("/{id}")
    public OperacionResponse<CarreraResponse> editar(@PathVariable Long id,
            @Valid @RequestBody CarreraRequest request) {
        return new OperacionResponse<>("Carrera actualizada", carreraService.editar(id, request));
    }

    // HU08
    @PatchMapping("/{id}/enviar-aprobacion")
    public OperacionResponse<CarreraResponse> enviarAAprobacion(@PathVariable Long id) {
        return new OperacionResponse<>("Carrera enviada a aprobación", carreraService.enviarAAprobacion(id));
    }

    // HU06
    @PatchMapping("/{id}/solicitar-desactivacion")
    public OperacionResponse<CarreraResponse> solicitarDesactivacion(@PathVariable Long id) {
        return new OperacionResponse<>("Solicitud de desactivación enviada", carreraService.solicitarDesactivacion(id));
    }

    // HU13
    @PatchMapping("/{id}/despublicar")
    public OperacionResponse<CarreraResponse> despublicar(@PathVariable Long id) {
        return new OperacionResponse<>("Carrera despublicada correctamente", carreraService.despublicar(id));
    }

    // HU12 (Secretario Académico): rechazar solicitud con motivo obligatorio
    @PatchMapping("/{id}/rechazar")
    public OperacionResponse<CarreraResponse> rechazar(@PathVariable Long id,
            @Valid @RequestBody RechazoRequest request) {
        return new OperacionResponse<>("Solicitud rechazada", carreraService.rechazar(id, request.motivo()));
    }
}
