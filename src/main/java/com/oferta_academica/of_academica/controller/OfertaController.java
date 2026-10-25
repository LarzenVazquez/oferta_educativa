package com.oferta_academica.of_academica.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oferta_academica.of_academica.entity.Carrera;
import com.oferta_academica.of_academica.entity.Division;
import com.oferta_academica.of_academica.entity.EstadoAprobacion;
import com.oferta_academica.of_academica.entity.Estatus;
import com.oferta_academica.of_academica.repository.CarreraRepository;
import com.oferta_academica.of_academica.repository.DivisionRepository;

// Rol: Administrador - HU07
@RestController
@RequestMapping("/api/admin/oferta")
public class OfertaController {

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    @GetMapping
    public List<Map<String, Object>> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long divisionId) {

        String busqueda = (nombre == null) ? "" : nombre.trim().toLowerCase();

        List<Division> divisiones;
        if (divisionId == null) {
            divisiones = divisionRepository.findAllByOrderByNombreAsc();
        } else {
            divisiones = divisionRepository.findAllById(List.of(divisionId));
        }

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Division division : divisiones) {
            if (division.getNombre().toLowerCase().contains(busqueda)) {
                resultado.add(fila("DIVISION", division.getId(), division.getNombre(), division.getNombre(),
                        division.getEstatus(), division.getEstadoAprobacion()));
            }

            for (Carrera carrera : carreraRepository.findByDivisionIdOrderByNombreAsc(division.getId())) {
                if (carrera.getNombre().toLowerCase().contains(busqueda)) {
                    resultado.add(fila("CARRERA", carrera.getId(), carrera.getNombre(), division.getNombre(),
                            carrera.getEstatus(), carrera.getEstadoAprobacion()));
                }
            }
        }
        return resultado;
    }

    private Map<String, Object> fila(String tipo, Long id, String nombre, String division,
            Estatus estatus, EstadoAprobacion estadoAprobacion) {
        Map<String, Object> fila = new LinkedHashMap<>();
        fila.put("tipo", tipo);
        fila.put("id", id);
        fila.put("nombre", nombre);
        fila.put("division", division);
        fila.put("estatus", estatus);
        fila.put("estadoAprobacion", estadoAprobacion);
        return fila;
    }
}
