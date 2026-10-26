package com.oferta_academica.of_academica.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oferta_academica.of_academica.entity.Carrera;
import com.oferta_academica.of_academica.entity.Division;
import com.oferta_academica.of_academica.entity.EstadoAprobacion;
import com.oferta_academica.of_academica.repository.CarreraRepository;
import com.oferta_academica.of_academica.repository.DivisionRepository;

// Pagina web publica: solo muestra lo que el Secretario aprobo (HU11).
// Se muestra un registro si esta publicado y aprobado. Mientras se revisa una solicitud de
// desactivacion sigue visible; al aprobarse (INACTIVO) desaparece. Un registro editado vuelve
// a BORRADOR y deja de verse hasta que se apruebe de nuevo. Una carrera solo se muestra si
// su division tambien se muestra.
@RestController
@RequestMapping("/api/public/oferta")
public class OfertaPublicaController {

    private static final List<EstadoAprobacion> VISIBLES =
            List.of(EstadoAprobacion.APROBADO, EstadoAprobacion.PENDIENTE_DESACTIVACION);

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    // HU14: Ver las divisiones y sus carreras en la página web
    @Transactional(readOnly = true)
    @GetMapping
    public List<Map<String, Object>> listar() {
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Division division : divisionRepository.findAllByOrderByNombreAsc()) {
            if (!visible(division.isPublicado(), division.getEstadoAprobacion())) {
                continue;
            }
            List<Map<String, Object>> carreras = new ArrayList<>();
            for (Carrera c : carreraRepository.findByDivisionIdOrderByNombreAsc(division.getId())) {
                if (visible(c.isPublicado(), c.getEstadoAprobacion())) {
                    Map<String, Object> mc = new LinkedHashMap<>();
                    mc.put("id", c.getId());
                    mc.put("nombre", c.getNombre());
                    mc.put("descripcion", c.getDescripcion());
                    mc.put("duracionCuatrimestres", c.getDuracionCuatrimestres());
                    mc.put("modalidad", c.getModalidad());
                    carreras.add(mc);
                }
            }
            Map<String, Object> md = new LinkedHashMap<>();
            md.put("id", division.getId());
            md.put("nombre", division.getNombre());
            md.put("descripcion", division.getDescripcion());
            md.put("carreras", carreras);
            resultado.add(md);
        }
        return resultado;
    }

    // HU15: Ver el detalle de una carrera (descripción, duración y modalidad)
    @Transactional(readOnly = true)
    @GetMapping("/carrera/{id}")
    public ResponseEntity<?> verDetalleCarrera(@PathVariable Long id) {
        Optional<Carrera> carreraOpt = carreraRepository.findById(id);
        
        if (carreraOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Carrera c = carreraOpt.get();
        Division d = c.getDivision();
        
        // Validar que tanto la carrera como su división sean visibles públicamente
        if (!visible(c.isPublicado(), c.getEstadoAprobacion()) || !visible(d.isPublicado(), d.getEstadoAprobacion())) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> mc = new LinkedHashMap<>();
        mc.put("id", c.getId());
        mc.put("nombre", c.getNombre());
        mc.put("descripcion", c.getDescripcion());
        mc.put("duracionCuatrimestres", c.getDuracionCuatrimestres());
        mc.put("modalidad", c.getModalidad());
        
        Map<String, Object> md = new LinkedHashMap<>();
        md.put("id", d.getId());
        md.put("nombre", d.getNombre());
        mc.put("division", md);

        return ResponseEntity.ok(mc);
    }

    private boolean visible(boolean publicado, EstadoAprobacion estado) {
        return publicado && VISIBLES.contains(estado);
    }
}