package com.oferta_academica.of_academica.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oferta_academica.of_academica.dto.CarreraResponse;
import com.oferta_academica.of_academica.dto.DivisionResponse;
import com.oferta_academica.of_academica.dto.OperacionResponse;
import com.oferta_academica.of_academica.dto.SolicitudResponse;
import com.oferta_academica.of_academica.entity.Carrera;
import com.oferta_academica.of_academica.entity.Division;
import com.oferta_academica.of_academica.entity.EstadoAprobacion;
import com.oferta_academica.of_academica.repository.CarreraRepository;
import com.oferta_academica.of_academica.repository.DivisionRepository;

// Rol: Secretario Académico - HU10 (bandeja de solicitudes), HU11 (aprobar)
@Service
@Transactional(readOnly = true)
public class SecretarioService {

    private static final List<EstadoAprobacion> EN_REVISION =
            List.of(EstadoAprobacion.PENDIENTE, EstadoAprobacion.PENDIENTE_DESACTIVACION);

    private final DivisionRepository divisionRepository;
    private final CarreraRepository carreraRepository;
    private final DivisionService divisionService;
    private final CarreraService carreraService;

    public SecretarioService(DivisionRepository divisionRepository, CarreraRepository carreraRepository,
            DivisionService divisionService, CarreraService carreraService) {
        this.divisionRepository = divisionRepository;
        this.carreraRepository = carreraRepository;
        this.divisionService = divisionService;
        this.carreraService = carreraService;
    }

    // HU10: registros en estado PENDIENTE o PENDIENTE_DESACTIVACION
    public List<SolicitudResponse> listarSolicitudes() {
        List<SolicitudResponse> lista = new ArrayList<>();
        divisionRepository.findByEstadoAprobacionInOrderByNombreAsc(EN_REVISION)
                .forEach(d -> lista.add(SolicitudResponse.de(d)));
        carreraRepository.findByEstadoAprobacionInOrderByNombreAsc(EN_REVISION)
                .forEach(c -> lista.add(SolicitudResponse.de(c)));
        return lista;
    }

    // HU11
    @Transactional
    public OperacionResponse<DivisionResponse> aprobarDivision(Long id) {
        Division division = divisionService.buscar(id);
        boolean esDesactivacion = division.getEstadoAprobacion() == EstadoAprobacion.PENDIENTE_DESACTIVACION;
        division.aprobar();
        String mensaje = esDesactivacion
                ? "División desactivada: ya no se muestra en la página web"
                : "División aprobada: ya se muestra en la página web";
        return new OperacionResponse<>(mensaje, DivisionResponse.de(division));
    }

    // HU11
    @Transactional
    public OperacionResponse<CarreraResponse> aprobarCarrera(Long id) {
        Carrera carrera = carreraService.buscar(id);
        boolean esDesactivacion = carrera.getEstadoAprobacion() == EstadoAprobacion.PENDIENTE_DESACTIVACION;
        carrera.aprobar();
        String mensaje = esDesactivacion
                ? "Carrera desactivada: ya no se muestra en la página web"
                : "Carrera aprobada: ya se muestra en la página web";
        return new OperacionResponse<>(mensaje, CarreraResponse.de(carrera));
    }
}
