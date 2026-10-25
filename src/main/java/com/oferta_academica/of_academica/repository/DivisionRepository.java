package com.oferta_academica.of_academica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oferta_academica.of_academica.entity.Division;

public interface DivisionRepository extends JpaRepository<Division, Long> {

    List<Division> findAllByOrderByNombreAsc();

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
