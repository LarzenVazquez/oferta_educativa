package com.oferta_academica.of_academica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oferta_academica.of_academica.entity.Carrera;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    List<Carrera> findAllByOrderByNombreAsc();

    boolean existsByNombreIgnoreCaseAndDivisionId(String nombre, Long divisionId);
}
