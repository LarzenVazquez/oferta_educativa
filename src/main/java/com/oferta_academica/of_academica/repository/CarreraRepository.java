<<<<<<< HEAD:src/main/java/com/of/oferta_educativa/repository/CarreraRepository.java
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
=======
package com.oferta_academica.of_academica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oferta_academica.of_academica.entity.Carrera;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    List<Carrera> findAllByOrderByNombreAsc();

    List<Carrera> findByDivisionIdOrderByNombreAsc(Long divisionId);

    boolean existsByNombreIgnoreCaseAndDivisionId(String nombre, Long divisionId);

    boolean existsByNombreIgnoreCaseAndDivisionIdAndIdNot(String nombre, Long divisionId, Long id);
}
>>>>>>> upstream/main:src/main/java/com/oferta_academica/of_academica/repository/CarreraRepository.java
