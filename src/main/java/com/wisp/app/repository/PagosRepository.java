package com.wisp.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wisp.app.entity.Pagos;

public interface PagosRepository extends JpaRepository<Pagos, Long> {

    List<Pagos> findByClienteId(Long clienteId);

    List<Pagos> findByEstado(String estado);

    boolean existsByClienteIdAndMesAndAnio(
            Long clienteId,
            Integer mes,
            Integer anio);
//Funcion para generar las fechas ala vista
    List<Pagos> findByAnioAndMes(Integer anio, Integer mes);
    
//top 5 pagos ordenados por fecha de pago descendente
    List<Pagos> findTop5ByOrderByFechaPagoDesc();
}