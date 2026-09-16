package com.wisp.app.repository;

import com.wisp.app.entity.Averia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AveriaRepository extends JpaRepository<Averia, Long> {
    List<Averia> findByEstado(String estado);

    // TOP 5 AVERIAS ORDENADOS POR FECHA DE ATENCION DESCENDENTE
    List<Averia> findTop5ByOrderByFechaAtencionDesc();

    // TOP 5 AVERIAS ORDENADOS POR FECHA DE REGISTRO DESCENDENTE
    List<Averia> findTop5ByOrderByFechaRegistroDesc();
}