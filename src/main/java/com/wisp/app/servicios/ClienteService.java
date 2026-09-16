package com.wisp.app.servicios;

import java.util.List;

import com.wisp.app.entity.Cliente;

public interface ClienteService {

    List<Cliente> listarClientes();

    Cliente guardarCliente(Cliente cliente);

    Cliente obtenerClientePorId(Long id);

    //void eliminarCliente(Long id);


    long totalClientes();

    List<Cliente> buscarPorEstado(String estado);

    void suspenderCliente(Long id);

}