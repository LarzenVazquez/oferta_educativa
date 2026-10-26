package com.oferta_academica.of_academica.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oferta_academica.of_academica.dto.CarreraDetallePublicaResponse;
import com.oferta_academica.of_academica.dto.OfertaItemResponse;
import com.oferta_academica.of_academica.dto.OfertaPublicaCarreraResponse;
import com.oferta_academica.of_academica.dto.OfertaPublicaDivisionResponse;
import com.oferta_academica.of_academica.entity.Carrera;
import com.oferta_academica.of_academica.entity.Division;
import com.oferta_academica.of_academica.exception.RecursoNoEncontradoException;
import com.oferta_academica.of_academica.repository.CarreraRepository;
import com.oferta_academica.of_academica.repository.DivisionRepository;

@Service
@Transactional(readOnly = true)
public class OfertaService {

    private final DivisionRepository divisionRepository;
    private final CarreraRepository carreraRepository;

    public OfertaService(DivisionRepository divisionRepository, CarreraRepository carreraRepository) {
        this.divisionRepository = divisionRepository;
        this.carreraRepository = carreraRepository;
    }

    // HU07: listado del Administrador, divisiones con sus carreras debajo. Filtros opcionales.
    public List<OfertaItemResponse> listarParaAdministrador(String nombre, Long divisionId) {
        String busqueda = (nombre == null) ? "" : nombre.trim().toLowerCase(Locale.ROOT);

        List<Division> divisiones = (divisionId == null)
                ? divisionRepository.findAllByOrderByNombreAsc()
                : divisionRepository.findById(divisionId).map(List::of).orElse(List.of());
        Map<Long, List<Carrera>> carrerasPorDivision = agruparPorDivision(
                (divisionId == null)
                        ? carreraRepository.findAllByOrderByNombreAsc()
                        : carreraRepository.findByDivisionIdOrderByNombreAsc(divisionId));

        List<OfertaItemResponse> resultado = new ArrayList<>();
        for (Division division : divisiones) {
            if (coincide(division.getNombre(), busqueda)) {
                resultado.add(OfertaItemResponse.de(division));
            }
            for (Carrera carrera : carrerasPorDivision.getOrDefault(division.getId(), List.of())) {
                if (coincide(carrera.getNombre(), busqueda)) {
                    resultado.add(OfertaItemResponse.de(carrera));
                }
            }
        }
        return resultado;
    }

    // Página pública: solo lo aprobado por el Secretario. Una carrera solo aparece si su división aparece.
    public List<OfertaPublicaDivisionResponse> listarPublica() {
        Map<Long, List<Carrera>> carrerasPorDivision = agruparPorDivision(carreraRepository.findAllByOrderByNombreAsc());

        List<OfertaPublicaDivisionResponse> resultado = new ArrayList<>();
        for (Division division : divisionRepository.findAllByOrderByNombreAsc()) {
            if (!division.esVisibleEnWeb()) {
                continue;
            }
            List<OfertaPublicaCarreraResponse> carreras = carrerasPorDivision
                    .getOrDefault(division.getId(), List.of()).stream()
                    .filter(Carrera::esVisibleEnWeb)
                    .map(OfertaPublicaCarreraResponse::de)
                    .toList();
            resultado.add(OfertaPublicaDivisionResponse.de(division, carreras));
        }
        return resultado;
    }

    // HU15: detalle público de una carrera. Solo si la carrera y su división se muestran en la web.
    public CarreraDetallePublicaResponse detalleCarreraPublica(Long id) {
        Carrera carrera = carreraRepository.findById(id)
                .filter(c -> c.esVisibleEnWeb() && c.getDivision().esVisibleEnWeb())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la carrera con id " + id));
        return CarreraDetallePublicaResponse.de(carrera);
    }

    private Map<Long, List<Carrera>> agruparPorDivision(List<Carrera> carreras) {
        return carreras.stream().collect(Collectors.groupingBy(c -> c.getDivision().getId()));
    }

    private boolean coincide(String texto, String busqueda) {
        return busqueda.isEmpty() || texto.toLowerCase(Locale.ROOT).contains(busqueda);
    }
}
