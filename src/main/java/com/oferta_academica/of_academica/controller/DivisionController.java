package com.of.oferta_educativa.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.of.oferta_educativa.entity.*;
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

    // HU02: datos actuales para el formulario
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Division> division = divisionRepository.findById(id);
        if (division.isEmpty()) {
            return mensaje(HttpStatus.NOT_FOUND, "No existe la división con id " + id);
        }
        return ResponseEntity.ok(division.get());
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

    // HU02: editar division
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @Valid @RequestBody Division datos,
            BindingResult validacion) {
        if (validacion.hasErrors()) {
            return erroresDeValidacion(validacion);
        }

        Optional<Division> encontrada = divisionRepository.findById(id);
        if (encontrada.isEmpty()) {
            return mensaje(HttpStatus.NOT_FOUND, "No existe la división con id " + id);
        }
        Division division = encontrada.get();

        if (tieneSolicitudPendiente(division)) {
            return mensaje(HttpStatus.CONFLICT, "La división tiene una solicitud en revisión");
        }
        if (divisionRepository.existsByNombreIgnoreCaseAndIdNot(datos.getNombre().trim(), id)) {
            return mensaje(HttpStatus.CONFLICT, "Ya existe otra división con ese nombre");
        }

        division.setNombre(datos.getNombre().trim());
        division.setDescripcion(datos.getDescripcion());
        division.setEstatus(datos.getEstatus());
        division.setEstadoAprobacion(EstadoAprobacion.BORRADOR);
        division.setMotivoRechazo(null);
        Division guardada = divisionRepository.save(division);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "División actualizada");
        respuesta.put("division", guardada);
        return ResponseEntity.ok(respuesta);
    }

    // HU03: solicitar desactivacion
    @PatchMapping("/{id}/solicitar-desactivacion")
    public ResponseEntity<?> solicitarDesactivacion(@PathVariable Long id) {
        Optional<Division> encontrada = divisionRepository.findById(id);
        if (encontrada.isEmpty()) {
            return mensaje(HttpStatus.NOT_FOUND, "No existe la división con id " + id);
        }
        Division division = encontrada.get();

        if (division.getEstadoAprobacion() == EstadoAprobacion.INACTIVO) {
            return mensaje(HttpStatus.CONFLICT, "La división ya está inactiva");
        }
        if (tieneSolicitudPendiente(division)) {
            return mensaje(HttpStatus.CONFLICT, "La división tiene una solicitud en revisión");
        }

        division.setEstadoAprobacion(EstadoAprobacion.PENDIENTE_DESACTIVACION);
        Division guardada = divisionRepository.save(division);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Solicitud de desactivación enviada");
        respuesta.put("division", guardada);
        return ResponseEntity.ok(respuesta);
    }

    // HU08: enviar a aprobacion (registro nuevo o modificado)
    @PatchMapping("/{id}/enviar-aprobacion")
    public ResponseEntity<?> enviarAAprobacion(@PathVariable Long id) {
        Optional<Division> encontrada = divisionRepository.findById(id);
        if (encontrada.isEmpty()) {
            return mensaje(HttpStatus.NOT_FOUND, "No existe la división con id " + id);
        }
        Division division = encontrada.get();

        EstadoAprobacion estado = division.getEstadoAprobacion();
        if (tieneSolicitudPendiente(division)) {
            return mensaje(HttpStatus.CONFLICT, "La división ya tiene una solicitud en revisión");
        }
        if (estado == EstadoAprobacion.APROBADO) {
            return mensaje(HttpStatus.CONFLICT, "La división ya está aprobada");
        }
        if (estado == EstadoAprobacion.INACTIVO) {
            return mensaje(HttpStatus.CONFLICT, "La división está inactiva y no se puede enviar a aprobación");
        }

        division.setEstadoAprobacion(EstadoAprobacion.PENDIENTE);
        division.setMotivoRechazo(null);
        Division guardada = divisionRepository.save(division);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Enviada a aprobación");
        respuesta.put("division", guardada);
        return ResponseEntity.ok(respuesta);
    }

    // Mientras el Secretario revisa, no se puede modificar
    private boolean tieneSolicitudPendiente(Division division) {
        return division.getEstadoAprobacion() == EstadoAprobacion.PENDIENTE
                || division.getEstadoAprobacion() == EstadoAprobacion.PENDIENTE_DESACTIVACION;
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
