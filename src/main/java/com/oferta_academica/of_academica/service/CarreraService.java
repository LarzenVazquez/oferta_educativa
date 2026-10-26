package com.oferta_academica.of_academica.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oferta_academica.of_academica.dto.CarreraRequest;
import com.oferta_academica.of_academica.dto.CarreraResponse;
import com.oferta_academica.of_academica.entity.Carrera;
import com.oferta_academica.of_academica.entity.Division;
import com.oferta_academica.of_academica.exception.RecursoNoEncontradoException;
import com.oferta_academica.of_academica.exception.ReglaNegocioException;
import com.oferta_academica.of_academica.repository.CarreraRepository;

// Rol: Administrador - HU04, HU05, HU06, HU08 (carreras). HU12: rechazo del Secretario
@Service
@Transactional(readOnly = true)
public class CarreraService {

    private final CarreraRepository carreraRepository;
    private final DivisionService divisionService;

    public CarreraService(CarreraRepository carreraRepository, DivisionService divisionService) {
        this.carreraRepository = carreraRepository;
        this.divisionService = divisionService;
    }

    public List<CarreraResponse> listar() {
        return carreraRepository.findAllByOrderByNombreAsc().stream().map(CarreraResponse::de).toList();
    }

    public CarreraResponse obtener(Long id) {
        return CarreraResponse.de(buscar(id));
    }

    // HU04: registrar carrera (queda en BORRADOR, no se publica)
    @Transactional
    public CarreraResponse registrar(CarreraRequest request) {
        Division division = divisionService.buscar(request.divisionId());
        if (division.estaInactivo()) {
            throw new ReglaNegocioException("No se pueden registrar carreras en una división inactiva");
        }
        String nombre = request.nombre().trim();
        if (carreraRepository.existsByNombreIgnoreCaseAndDivisionId(nombre, division.getId())) {
            throw new ReglaNegocioException("Ya existe una carrera con ese nombre en esa división");
        }
        Carrera carrera = new Carrera(nombre, request.descripcion().trim(), request.duracionCuatrimestres(),
                request.modalidad(), division);
        return CarreraResponse.de(carreraRepository.save(carrera));
    }

    // HU05: editar carrera
    @Transactional
    public CarreraResponse editar(Long id, CarreraRequest request) {
        Carrera carrera = buscar(id);
        if (carrera.tieneSolicitudPendiente()) {
            throw new ReglaNegocioException("La carrera tiene una solicitud en revisión");
        }
        Division division = divisionService.buscar(request.divisionId());
        if (division.estaInactivo()) {
            throw new ReglaNegocioException("No se puede mover la carrera a una división inactiva");
        }
        String nombre = request.nombre().trim();
        if (carreraRepository.existsByNombreIgnoreCaseAndDivisionIdAndIdNot(nombre, division.getId(), id)) {
            throw new ReglaNegocioException("Ya existe otra carrera con ese nombre en esa división");
        }
        carrera.actualizarDatos(nombre, request.descripcion().trim(), request.duracionCuatrimestres(),
                request.modalidad(), division);
        return CarreraResponse.de(carrera);
    }

    // HU08: enviar a aprobación
    @Transactional
    public CarreraResponse enviarAAprobacion(Long id) {
        Carrera carrera = buscar(id);
        carrera.enviarAAprobacion();
        return CarreraResponse.de(carrera);
    }

    // HU06: solicitar desactivación
    @Transactional
    public CarreraResponse solicitarDesactivacion(Long id) {
        Carrera carrera = buscar(id);
        carrera.solicitarDesactivacion();
        return CarreraResponse.de(carrera);
    }

    // HU12: rechazar solicitud con motivo obligatorio
    @Transactional
    public CarreraResponse rechazar(Long id, String motivo) {
        Carrera carrera = buscar(id);
        carrera.rechazar(motivo.trim());
        return CarreraResponse.de(carrera);
    }

    // HU13: despublicar carrera
    @Transactional
    public CarreraResponse despublicar(Long id) {
        Carrera carrera = buscar(id);
        carrera.despublicar();
        return CarreraResponse.de(carrera);
    }

    public Carrera buscar(Long id) {
        return carreraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la carrera con id " + id));
    }
}
