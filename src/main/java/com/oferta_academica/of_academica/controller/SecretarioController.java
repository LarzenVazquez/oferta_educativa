package com.of.oferta_educativa.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.of.oferta_educativa.entity.Carrera;
import com.of.oferta_educativa.entity.Division;
import com.of.oferta_educativa.entity.EstadoAprobacion;
import com.of.oferta_educativa.entity.Estatus;
import com.of.oferta_educativa.repository.CarreraRepository;
import com.of.oferta_educativa.repository.DivisionRepository;

@RestController
@RequestMapping("/api/admin/secretario")
public class SecretarioController {

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    @GetMapping("/solicitudes")
    public ResponseEntity<?> listarPendientes() {
        try {
            List<Map<String, Object>> lista = new ArrayList<>();

            // Buscar divisiones pendientes
            List<Division> divs = divisionRepository.findByEstadoAprobacionIn(
                List.of(EstadoAprobacion.PENDIENTE, EstadoAprobacion.PENDIENTE_DESACTIVACION)
            );
            if (divs != null) {
                for (Division d : divs) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", d.getId());
                    map.put("tipo", "DIVISION");
                    map.put("nombre", d.getNombre());
                    map.put("descripcion", d.getDescripcion());
                    map.put("estadoAprobacion", d.getEstadoAprobacion());
                    map.put("tipoSolicitud", d.getEstadoAprobacion() == EstadoAprobacion.PENDIENTE_DESACTIVACION ? "Desactivación" : "Publicación / Modificación");
                    lista.add(map);
                }
            }

            // Buscar carreras pendientes
            List<Carrera> cars = carreraRepository.findByEstadoAprobacionIn(
                List.of(EstadoAprobacion.PENDIENTE, EstadoAprobacion.PENDIENTE_DESACTIVACION)
            );
            if (cars != null) {
                for (Carrera c : cars) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", c.getId());
                    map.put("tipo", "CARRERA");
                    map.put("nombre", c.getNombre());
                    map.put("descripcion", c.getDescripcion());
                    map.put("estadoAprobacion", c.getEstadoAprobacion());
                    map.put("tipoSolicitud", c.getEstadoAprobacion() == EstadoAprobacion.PENDIENTE_DESACTIVACION ? "Desactivación" : "Publicación / Modificación");
                    lista.add(map);
                }
            }

            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("mensaje", "Error en el servidor: " + e.getMessage()));
        }
    }

    @PatchMapping("/divisiones/{id}/aprobar")
    public ResponseEntity<?> aprobarDivision(@PathVariable Long id) {
        Optional<Division> opt = divisionRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Division d = opt.get();

        if (d.getEstadoAprobacion() == EstadoAprobacion.PENDIENTE_DESACTIVACION) {
            d.setEstadoAprobacion(EstadoAprobacion.INACTIVO);
            d.setEstatus(Estatus.INACTIVO);
            d.setPublicado(false);
        } else {
            d.setEstadoAprobacion(EstadoAprobacion.APROBADO);
            d.setPublicado(true);
        }
        d.setMotivoRechazo(null);
        divisionRepository.save(d);
        return ResponseEntity.ok(Map.of("mensaje", "División aprobada exitosamente"));
    }

    @PatchMapping("/divisiones/{id}/rechazar")
    public ResponseEntity<?> rechazarDivision(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Optional<Division> opt = divisionRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Division d = opt.get();

        d.setEstadoAprobacion(EstadoAprobacion.RECHAZADO);
        d.setMotivoRechazo(body.get("motivoRechazo"));
        divisionRepository.save(d);
        return ResponseEntity.ok(Map.of("mensaje", "Solicitud rechazada"));
    }

    @PatchMapping("/carreras/{id}/aprobar")
    public ResponseEntity<?> aprobarCarrera(@PathVariable Long id) {
        Optional<Carrera> opt = carreraRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Carrera c = opt.get();

        if (c.getEstadoAprobacion() == EstadoAprobacion.PENDIENTE_DESACTIVACION) {
            c.setEstadoAprobacion(EstadoAprobacion.INACTIVO);
            c.setEstatus(Estatus.INACTIVO);
            c.setPublicado(false);
        } else {
            c.setEstadoAprobacion(EstadoAprobacion.APROBADO);
            c.setPublicado(true);
        }
        c.setMotivoRechazo(null);
        carreraRepository.save(c);
        return ResponseEntity.ok(Map.of("mensaje", "Carrera aprobada exitosamente"));
    }

    @PatchMapping("/carreras/{id}/rechazar")
    public ResponseEntity<?> rechazarCarrera(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Optional<Carrera> opt = carreraRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Carrera c = opt.get();

        c.setEstadoAprobacion(EstadoAprobacion.RECHAZADO);
        c.setMotivoRechazo(body.get("motivoRechazo"));
        carreraRepository.save(c);
        return ResponseEntity.ok(Map.of("mensaje", "Solicitud rechazada"));
    }
}