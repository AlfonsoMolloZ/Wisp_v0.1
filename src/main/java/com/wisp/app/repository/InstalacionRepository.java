package com.wisp.app.repository;

import com.wisp.app.entity.Instalacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InstalacionRepository extends JpaRepository<Instalacion, Long> {
    List<Instalacion> findByEstado(String estado);
    List<Instalacion> findTop5ByOrderByFechaAtencionDesc();
}