package com.oferta_academica.of_academica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.oferta_academica.of_academica.entity.Carrera;
import com.oferta_academica.of_academica.entity.EstadoAprobacion;

// @EntityGraph carga la división en la misma consulta (evita N+1 al armar las respuestas)
public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    @EntityGraph(attributePaths = "division")
    List<Carrera> findAllByOrderByNombreAsc();

    @EntityGraph(attributePaths = "division")
    List<Carrera> findByDivisionIdOrderByNombreAsc(Long divisionId);

    @EntityGraph(attributePaths = "division")
    List<Carrera> findByEstadoAprobacionInOrderByNombreAsc(List<EstadoAprobacion> estados);

    boolean existsByNombreIgnoreCaseAndDivisionId(String nombre, Long divisionId);

    boolean existsByNombreIgnoreCaseAndDivisionIdAndIdNot(String nombre, Long divisionId, Long id);
}
