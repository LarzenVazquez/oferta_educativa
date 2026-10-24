package com.of.oferta_educativa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.of.oferta_educativa.entity.Division;

public interface DivisionRepository extends JpaRepository<Division, Long> {

    List<Division> findAllByOrderByNombreAsc();

    boolean existsByNombreIgnoreCase(String nombre);
}
