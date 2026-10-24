package com.of.oferta_educativa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.of.oferta_educativa.entity.Carrera;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {

  List<Carrera> findAllByOrderByNombreAsc();

  boolean existsByNombreIgnoreCaseAndDivisionId(String nombre, Long divisionId);

  boolean existsByNombreIgnoreCaseAndDivisionIdAndIdNot(String nombre, Long divisionId, Long id);
}
