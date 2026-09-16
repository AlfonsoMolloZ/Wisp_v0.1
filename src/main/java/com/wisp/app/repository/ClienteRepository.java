package com.wisp.app.repository;

import com.wisp.app.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByEstado(String estado);

    boolean existsByCorreo(String correo);

    boolean existsByDni(String dni);

    // Buscar clientes que pertenecen a una red WAN
    List<Cliente> findByRedWanId(Long id);

}