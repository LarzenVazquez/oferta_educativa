package com.oferta_academica.of_academica.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oferta_academica.of_academica.dto.DivisionRequest;
import com.oferta_academica.of_academica.dto.DivisionResponse;
import com.oferta_academica.of_academica.entity.Division;
import com.oferta_academica.of_academica.exception.RecursoNoEncontradoException;
import com.oferta_academica.of_academica.exception.ReglaNegocioException;
import com.oferta_academica.of_academica.repository.DivisionRepository;

// Rol: Administrador - HU01, HU02, HU03, HU08 (divisiones). HU12: rechazo del Secretario
@Service
@Transactional(readOnly = true)
public class DivisionService {

    private final DivisionRepository divisionRepository;

    public DivisionService(DivisionRepository divisionRepository) {
        this.divisionRepository = divisionRepository;
    }

    public List<DivisionResponse> listar() {
        return divisionRepository.findAllByOrderByNombreAsc().stream().map(DivisionResponse::de).toList();
    }

    public DivisionResponse obtener(Long id) {
        return DivisionResponse.de(buscar(id));
    }

    // HU01: registrar división (queda en BORRADOR, no se publica)
    @Transactional
    public DivisionResponse registrar(DivisionRequest request) {
        String nombre = request.nombre().trim();
        if (divisionRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("Ya existe una división con ese nombre");
        }
        Division division = new Division(nombre, request.descripcion().trim(), request.estatus());
        return DivisionResponse.de(divisionRepository.save(division));
    }

    // HU02: editar división
    @Transactional
    public DivisionResponse editar(Long id, DivisionRequest request) {
        Division division = buscar(id);
        String nombre = request.nombre().trim();
        if (divisionRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ReglaNegocioException("Ya existe otra división con ese nombre");
        }
        division.actualizarDatos(nombre, request.descripcion().trim(), request.estatus());
        return DivisionResponse.de(division);
    }

    // HU08: enviar a aprobación
    @Transactional
    public DivisionResponse enviarAAprobacion(Long id) {
        Division division = buscar(id);
        division.enviarAAprobacion();
        return DivisionResponse.de(division);
    }

    // HU03: solicitar desactivación
    @Transactional
    public DivisionResponse solicitarDesactivacion(Long id) {
        Division division = buscar(id);
        division.solicitarDesactivacion();
        return DivisionResponse.de(division);
    }

    // HU12: rechazar solicitud con motivo obligatorio
    @Transactional
    public DivisionResponse rechazar(Long id, String motivo) {
        Division division = buscar(id);
        division.rechazar(motivo.trim());
        return DivisionResponse.de(division);
    }

    public Division buscar(Long id) {
        return divisionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la división con id " + id));
    }
}
