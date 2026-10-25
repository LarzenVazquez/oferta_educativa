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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    // HU05: datos actuales para el formulario
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Carrera> carrera = carreraRepository.findById(id);
        if (carrera.isEmpty()) {
            return mensaje(HttpStatus.NOT_FOUND, "No existe la carrera con id " + id);
        }
        return ResponseEntity.ok(carrera.get());
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

    // HU05: editar carrera
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @Valid @RequestBody Carrera datos,
            BindingResult validacion) {
        if (validacion.hasErrors()) {
            return erroresDeValidacion(validacion);
        }

        Optional<Carrera> encontrada = carreraRepository.findById(id);
        if (encontrada.isEmpty()) {
            return mensaje(HttpStatus.NOT_FOUND, "No existe la carrera con id " + id);
        }
        Carrera carrera = encontrada.get();

        if (tieneSolicitudPendiente(carrera)) {
            return mensaje(HttpStatus.CONFLICT, "La carrera tiene una solicitud en revisión");
        }

        Long divisionId = datos.getDivision().getId();
        if (divisionId == null) {
            return mensaje(HttpStatus.BAD_REQUEST, "La división es obligatoria");
        }
        Optional<Division> division = divisionRepository.findById(divisionId);
        if (division.isEmpty()) {
            return mensaje(HttpStatus.NOT_FOUND, "No existe la división con id " + divisionId);
        }
        if (division.get().getEstadoAprobacion() == EstadoAprobacion.INACTIVO) {
            return mensaje(HttpStatus.CONFLICT, "No se puede mover la carrera a una división inactiva");
        }
        if (carreraRepository.existsByNombreIgnoreCaseAndDivisionIdAndIdNot(datos.getNombre().trim(), divisionId, id)) {
            return mensaje(HttpStatus.CONFLICT, "Ya existe otra carrera con ese nombre en esa división");
        }

        carrera.setNombre(datos.getNombre().trim());
        carrera.setDescripcion(datos.getDescripcion());
        carrera.setDuracionCuatrimestres(datos.getDuracionCuatrimestres());
        carrera.setModalidad(datos.getModalidad());
        carrera.setDivision(division.get());
        carrera.setEstadoAprobacion(EstadoAprobacion.BORRADOR);
        carrera.setMotivoRechazo(null);
        Carrera guardada = carreraRepository.save(carrera);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Carrera actualizada");
        respuesta.put("carrera", guardada);
        return ResponseEntity.ok(respuesta);
    }

    // HU06: solicitar desactivacion
    @PatchMapping("/{id}/solicitar-desactivacion")
    public ResponseEntity<?> solicitarDesactivacion(@PathVariable Long id) {
        Optional<Carrera> encontrada = carreraRepository.findById(id);
        if (encontrada.isEmpty()) {
            return mensaje(HttpStatus.NOT_FOUND, "No existe la carrera con id " + id);
        }
        Carrera carrera = encontrada.get();

        if (carrera.getEstadoAprobacion() == EstadoAprobacion.INACTIVO) {
            return mensaje(HttpStatus.CONFLICT, "La carrera ya está inactiva");
        }
        if (tieneSolicitudPendiente(carrera)) {
            return mensaje(HttpStatus.CONFLICT, "La carrera tiene una solicitud en revisión");
        }

        carrera.setEstadoAprobacion(EstadoAprobacion.PENDIENTE_DESACTIVACION);
        Carrera guardada = carreraRepository.save(carrera);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Solicitud de desactivación enviada");
        respuesta.put("carrera", guardada);
        return ResponseEntity.ok(respuesta);
    }

    // HU12 (Secretario Académico): rechazar solicitud con motivo obligatorio
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazar(@PathVariable Long id, @RequestBody(required = false) Map<String, String> datos) {
        String motivo = (datos == null || datos.get("motivo") == null) ? "" : datos.get("motivo").trim();
        if (motivo.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("motivo", "El motivo de rechazo es obligatorio"));
        }
        if (motivo.length() > 500) {
            return ResponseEntity.badRequest().body(Map.of("motivo", "El motivo no puede exceder 500 caracteres"));
        }

        Optional<Carrera> encontrada = carreraRepository.findById(id);
        if (encontrada.isEmpty()) {
            return mensaje(HttpStatus.NOT_FOUND, "No existe la carrera con id " + id);
        }
        Carrera carrera = encontrada.get();

        if (!tieneSolicitudPendiente(carrera)) {
            return mensaje(HttpStatus.CONFLICT, "La carrera no tiene una solicitud pendiente");
        }

        // Publicación rechazada: no aparece en la web.
        // Desactivación rechazada: se conserva "publicado" tal cual (sigue visible).
        if (carrera.getEstadoAprobacion() == EstadoAprobacion.PENDIENTE) {
            carrera.setPublicado(false);
        }
        carrera.setEstadoAprobacion(EstadoAprobacion.RECHAZADO);
        carrera.setMotivoRechazo(motivo);
        Carrera guardada = carreraRepository.save(carrera);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Solicitud rechazada");
        respuesta.put("carrera", guardada);
        return ResponseEntity.ok(respuesta);
    }

    // Mientras el Secretario revisa, no se puede modificar
    private boolean tieneSolicitudPendiente(Carrera carrera) {
        return carrera.getEstadoAprobacion() == EstadoAprobacion.PENDIENTE
                || carrera.getEstadoAprobacion() == EstadoAprobacion.PENDIENTE_DESACTIVACION;
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
