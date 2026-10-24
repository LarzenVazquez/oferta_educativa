package com.oferta_academica.of_academica.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oferta_academica.of_academica.entity.Carrera;
import com.oferta_academica.of_academica.entity.Division;
import com.oferta_academica.of_academica.entity.EstadoAprobacion;
import com.oferta_academica.of_academica.entity.Estatus;
import com.oferta_academica.of_academica.repository.CarreraRepository;
import com.oferta_academica.of_academica.repository.DivisionRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/carreras")
public class CarreraController {

    @Autowired
    private CarreraRepository carreraRepository;

    @Autowired
    private DivisionRepository divisionRepository;

    // Listado de carreras
    @GetMapping
    public List<Carrera> listar() {
        return carreraRepository.findAllByOrderByNombreAsc();
    }

    // HU04: registrar carrera
    @PostMapping
    public ResponseEntity<?> registrar(@Valid @RequestBody Carrera carrera, BindingResult validacion) {
        if (validacion.hasErrors()) {
            return erroresDeValidacion(validacion);
        }

        Long divisionId = carrera.getDivision().getId();
        if (divisionId == null) {
            return mensaje(HttpStatus.BAD_REQUEST, "La división es obligatoria");
        }
        Optional<Division> division = divisionRepository.findById(divisionId);
        if (division.isEmpty()) {
            return mensaje(HttpStatus.NOT_FOUND, "No existe la división con id " + divisionId);
        }
        if (division.get().getEstadoAprobacion() == EstadoAprobacion.INACTIVO) {
            return mensaje(HttpStatus.CONFLICT, "No se pueden registrar carreras en una división inactiva");
        }
        if (carreraRepository.existsByNombreIgnoreCaseAndDivisionId(carrera.getNombre().trim(), divisionId)) {
            return mensaje(HttpStatus.CONFLICT, "Ya existe una carrera con ese nombre en esa división");
        }

        carrera.setId(null);
        carrera.setNombre(carrera.getNombre().trim());
        carrera.setDivision(division.get());
        carrera.setEstatus(Estatus.ACTIVO);
        carrera.setEstadoAprobacion(EstadoAprobacion.BORRADOR);
        carrera.setPublicado(false);
        carrera.setMotivoRechazo(null);
        Carrera guardada = carreraRepository.save(carrera);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Carrera registrada");
        respuesta.put("carrera", guardada);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    private ResponseEntity<?> mensaje(HttpStatus estado, String texto) {
        return ResponseEntity.status(estado).body(Map.of("mensaje", texto));
    }

    private ResponseEntity<?> erroresDeValidacion(BindingResult validacion) {
        Map<String, String> errores = new HashMap<>();
        validacion.getFieldErrors().forEach(e -> errores.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errores);
    }
}
