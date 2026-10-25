package com.oferta_academica.of_academica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oferta_academica.of_academica.entity.Carrera;
import com.oferta_academica.of_academica.entity.EstadoAprobacion;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    List<Carrera> findAllByOrderByNombreAsc();

    List<Carrera> findByDivisionIdOrderByNombreAsc(Long divisionId);

    List<Carrera> findByEstadoAprobacionIn(List<EstadoAprobacion> estados);

    boolean existsByNombreIgnoreCaseAndDivisionId(String nombre, Long divisionId);

    boolean existsByNombreIgnoreCaseAndDivisionIdAndIdNot(String nombre, Long divisionId, Long id);
}
