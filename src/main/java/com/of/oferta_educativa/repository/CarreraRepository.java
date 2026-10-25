package com.of.oferta_educativa.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.of.oferta_educativa.entity.Carrera;
import com.of.oferta_educativa.entity.EstadoAprobacion;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    List<Carrera> findAllByOrderByNombreAsc();

    List<Carrera> findByEstadoAprobacionIn(List<EstadoAprobacion> estados);

    boolean existsByNombreIgnoreCaseAndDivisionId(String nombre, Long divisionId);
}
