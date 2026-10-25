package com.of.oferta_educativa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.of.oferta_educativa.entity.Division;
import com.of.oferta_educativa.entity.EstadoAprobacion;

public interface DivisionRepository extends JpaRepository<Division, Long> {

    List<Division> findAllByOrderByNombreAsc();

    List<Division> findByEstadoAprobacionIn(List<EstadoAprobacion> estados);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
