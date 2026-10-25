package com.oferta_academica.of_academica.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oferta_academica.of_academica.entity.Carrera;
import com.oferta_academica.of_academica.entity.Division;
import com.oferta_academica.of_academica.entity.EstadoAprobacion;
import com.oferta_academica.of_academica.entity.Estatus;
import com.oferta_academica.of_academica.repository.CarreraRepository;
import com.oferta_academica.of_academica.repository.DivisionRepository;

// Rol: Secretario - HU10/HU11
// Aprobar una solicitud del Administrador decide que se muestra y que se retira de la pagina web:
//   - PENDIENTE (publicacion)              -> APROBADO, publicado = true  (aparece en la web)
//   - PENDIENTE_DESACTIVACION (desactivar) -> INACTIVO,  publicado = false (deja de mostrarse)
@RestController
@RequestMapping("/api/admin/secretario")
public class SecretarioController {

    private static final List<EstadoAprobacion> EN_REVISION =
            List.of(EstadoAprobacion.PENDIENTE, EstadoAprobacion.PENDIENTE_DESACTIVACION);

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    // ---------- Bandeja de solicitudes ----------
    @Transactional(readOnly = true)
    @GetMapping("/solicitudes")
    public List<Map<String, Object>> listarPendientes() {
        List<Map<String, Object>> lista = new ArrayList<>();

        for (Division d : divisionRepository.findByEstadoAprobacionIn(EN_REVISION)) {
            lista.add(fila("DIVISION", d.getId(), d.getNombre(), d.getDescripcion(), d.getNombre(),
                    d.getEstadoAprobacion()));
        }
        for (Carrera c : carreraRepository.findByEstadoAprobacionIn(EN_REVISION)) {
            lista.add(fila("CARRERA", c.getId(), c.getNombre(), c.getDescripcion(), c.getDivision().getNombre(),
                    c.getEstadoAprobacion()));
        }
        return lista;
    }

    // ---------- Divisiones ----------
    @Transactional
    @PatchMapping("/divisiones/{id}/aprobar")
    public ResponseEntity<?> aprobarDivision(@PathVariable Long id) {
        Optional<Division> encontrada = divisionRepository.findById(id);
        if (encontrada.isEmpty()) {
            return mensaje(HttpStatus.NOT_FOUND, "No existe la división con id " + id);
        }
        Division d = encontrada.get();

        switch (d.getEstadoAprobacion()) {
            case PENDIENTE -> {
                d.setEstadoAprobacion(EstadoAprobacion.APROBADO);
                d.setPublicado(true);
                d.setMotivoRechazo(null);
                divisionRepository.save(d);
                return resultado("División aprobada: ya se muestra en la página web", d.getEstadoAprobacion());
            }
            case PENDIENTE_DESACTIVACION -> {
                d.setEstadoAprobacion(EstadoAprobacion.INACTIVO);
                d.setEstatus(Estatus.INACTIVO);
                d.setPublicado(false);
                d.setMotivoRechazo(null);
                divisionRepository.save(d);
                return resultado("División desactivada: ya no se muestra en la página web", d.getEstadoAprobacion());
            }
            default -> {
                return mensaje(HttpStatus.CONFLICT, "La división no tiene una solicitud pendiente de revisión");
            }
        }
    }

    @Transactional
    @PatchMapping("/divisiones/{id}/rechazar")
    public ResponseEntity<?> rechazarDivision(@PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String motivo = motivoDe(body);
        if (motivo == null) {
            return mensaje(HttpStatus.BAD_REQUEST, "El motivo de rechazo es obligatorio");
        }
        Optional<Division> encontrada = divisionRepository.findById(id);
        if (encontrada.isEmpty()) {
            return mensaje(HttpStatus.NOT_FOUND, "No existe la división con id " + id);
        }
        Division d = encontrada.get();

        if (d.getEstadoAprobacion() == EstadoAprobacion.PENDIENTE) {
            d.setEstadoAprobacion(EstadoAprobacion.RECHAZADO);
            d.setPublicado(false);
            d.setMotivoRechazo(motivo);
        } else if (d.getEstadoAprobacion() == EstadoAprobacion.PENDIENTE_DESACTIVACION) {
            // Se niega la baja: si ya estaba publicada se queda publicada
            if (d.isPublicado()) {
                d.setEstadoAprobacion(EstadoAprobacion.APROBADO);
                d.setMotivoRechazo(null);
            } else {
                d.setEstadoAprobacion(EstadoAprobacion.RECHAZADO);
                d.setMotivoRechazo(motivo);
            }
        } else {
            return mensaje(HttpStatus.CONFLICT, "La división no tiene una solicitud pendiente de revisión");
        }
        divisionRepository.save(d);
        return resultado("Solicitud rechazada", d.getEstadoAprobacion());
    }

    // ---------- Carreras ----------
    @Transactional
    @PatchMapping("/carreras/{id}/aprobar")
    public ResponseEntity<?> aprobarCarrera(@PathVariable Long id) {
        Optional<Carrera> encontrada = carreraRepository.findById(id);
        if (encontrada.isEmpty()) {
            return mensaje(HttpStatus.NOT_FOUND, "No existe la carrera con id " + id);
        }
        Carrera c = encontrada.get();

        switch (c.getEstadoAprobacion()) {
            case PENDIENTE -> {
                c.setEstadoAprobacion(EstadoAprobacion.APROBADO);
                c.setPublicado(true);
                c.setMotivoRechazo(null);
                carreraRepository.save(c);
                return resultado("Carrera aprobada: ya se muestra en la página web", c.getEstadoAprobacion());
            }
            case PENDIENTE_DESACTIVACION -> {
                c.setEstadoAprobacion(EstadoAprobacion.INACTIVO);
                c.setEstatus(Estatus.INACTIVO);
                c.setPublicado(false);
                c.setMotivoRechazo(null);
                carreraRepository.save(c);
                return resultado("Carrera desactivada: ya no se muestra en la página web", c.getEstadoAprobacion());
            }
            default -> {
                return mensaje(HttpStatus.CONFLICT, "La carrera no tiene una solicitud pendiente de revisión");
            }
        }
    }

    @Transactional
    @PatchMapping("/carreras/{id}/rechazar")
    public ResponseEntity<?> rechazarCarrera(@PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String motivo = motivoDe(body);
        if (motivo == null) {
            return mensaje(HttpStatus.BAD_REQUEST, "El motivo de rechazo es obligatorio");
        }
        Optional<Carrera> encontrada = carreraRepository.findById(id);
        if (encontrada.isEmpty()) {
            return mensaje(HttpStatus.NOT_FOUND, "No existe la carrera con id " + id);
        }
        Carrera c = encontrada.get();

        if (c.getEstadoAprobacion() == EstadoAprobacion.PENDIENTE) {
            c.setEstadoAprobacion(EstadoAprobacion.RECHAZADO);
            c.setPublicado(false);
            c.setMotivoRechazo(motivo);
        } else if (c.getEstadoAprobacion() == EstadoAprobacion.PENDIENTE_DESACTIVACION) {
            if (c.isPublicado()) {
                c.setEstadoAprobacion(EstadoAprobacion.APROBADO);
                c.setMotivoRechazo(null);
            } else {
                c.setEstadoAprobacion(EstadoAprobacion.RECHAZADO);
                c.setMotivoRechazo(motivo);
            }
        } else {
            return mensaje(HttpStatus.CONFLICT, "La carrera no tiene una solicitud pendiente de revisión");
        }
        carreraRepository.save(c);
        return resultado("Solicitud rechazada", c.getEstadoAprobacion());
    }

    // ---------- Utilidades ----------
    private Map<String, Object> fila(String tipo, Long id, String nombre, String descripcion, String division,
            EstadoAprobacion estado) {
        Map<String, Object> fila = new LinkedHashMap<>();
        fila.put("id", id);
        fila.put("tipo", tipo);
        fila.put("nombre", nombre);
        fila.put("descripcion", descripcion);
        fila.put("division", division);
        fila.put("estadoAprobacion", estado);
        fila.put("tipoSolicitud", estado == EstadoAprobacion.PENDIENTE_DESACTIVACION ? "Desactivación" : "Publicación");
        return fila;
    }

    private String motivoDe(Map<String, String> body) {
        if (body == null || body.get("motivoRechazo") == null) {
            return null;
        }
        String motivo = body.get("motivoRechazo").trim();
        if (motivo.isEmpty()) {
            return null;
        }
        return motivo.length() > 500 ? motivo.substring(0, 500) : motivo;
    }

    private ResponseEntity<?> resultado(String texto, EstadoAprobacion estado) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("mensaje", texto);
        r.put("estadoAprobacion", estado);
        return ResponseEntity.ok(r);
    }

    private ResponseEntity<?> mensaje(HttpStatus estado, String texto) {
        return ResponseEntity.status(estado).body(Map.of("mensaje", texto));
    }
}
