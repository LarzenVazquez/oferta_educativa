package com.of.oferta_educativa.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.of.oferta_educativa.entity.Division;
import com.of.oferta_educativa.entity.EstadoAprobacion;
import com.of.oferta_educativa.repository.DivisionRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/divisiones")
public class DivisionController {

    @Autowired
    private DivisionRepository divisionRepository;

    // Listado de divisiones
    @GetMapping
    public List<Division> listar() {
        return divisionRepository.findAllByOrderByNombreAsc();
    }

    // HU01: registrar division
    @PostMapping
    public ResponseEntity<?> registrar(@Valid @RequestBody Division division, BindingResult validacion) {
        if (validacion.hasErrors()) {
            return erroresDeValidacion(validacion);
        }
        if (divisionRepository.existsByNombreIgnoreCase(division.getNombre().trim())) {
            return mensaje(HttpStatus.CONFLICT, "Ya existe una división con ese nombre");
        }

        division.setId(null);
        division.setNombre(division.getNombre().trim());
        division.setEstadoAprobacion(EstadoAprobacion.BORRADOR);
        division.setPublicado(false);
        division.setMotivoRechazo(null);
        Division guardada = divisionRepository.save(division);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "División registrada");
        respuesta.put("division", guardada);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    private ResponseEntity<?> mensaje(HttpStatus estado, String texto) {
        return ResponseEntity.status(estado).body(Map.of("mensaje", texto));
    }

    // Regresa { "nombre": "El nombre es obligatorio", ... }
    private ResponseEntity<?> erroresDeValidacion(BindingResult validacion) {
        Map<String, String> errores = new HashMap<>();
        validacion.getFieldErrors().forEach(e -> errores.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errores);
    }
}
