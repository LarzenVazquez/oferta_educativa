package com.oferta_academica.of_academica.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
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

    private boolean visible(boolean publicado, EstadoAprobacion estado) {
        return publicado && VISIBLES.contains(estado);
    }
}
