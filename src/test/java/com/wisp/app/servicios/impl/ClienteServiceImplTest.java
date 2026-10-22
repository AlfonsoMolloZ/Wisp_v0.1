package com.wisp.app.servicios.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wisp.app.entity.Cliente;
import com.wisp.app.entity.RedWan;
import com.wisp.app.repository.ClienteRepository;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    private ClienteServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ClienteServiceImpl(clienteRepository);
    }

    @Test
    void generaPrimeraIpLibreAlCrearCliente() {
        RedWan red = redWan();
        Cliente existente = cliente(red);
        existente.setIp("192.168.10.2");

        when(clienteRepository.findByRedWanId(1L)).thenReturn(List.of(existente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        Cliente nuevo = cliente(red);
        Cliente guardado = service.guardarCliente(nuevo);

        assertThat(guardado.getIp()).isEqualTo("192.168.10.3");
        assertThat(guardado.getEstado()).isEqualTo("Nuevo");
    }

    @Test
    void respetaIpIngresadaManualmente() {
        Cliente nuevo = cliente(redWan());
        nuevo.setIp("192.168.10.50");

        service.guardarCliente(nuevo);

        assertThat(nuevo.getIp()).isEqualTo("192.168.10.50");
        verify(clienteRepository, never()).findByRedWanId(anyLong());
    }

    private RedWan redWan() {
        RedWan red = new RedWan();
        red.setId(1L);
        red.setNombre("Red principal");
        red.setDireccionRed("192.168.10.0");
        red.setMascara("255.255.255.0");
        return red;
    }

    private Cliente cliente(RedWan red) {
        Cliente cliente = new Cliente();
        cliente.setNombres("Ana");
        cliente.setApellidos("Perez");
        cliente.setDni("12345678");
        cliente.setTelefono("999999999");
        cliente.setDireccion("Calle 1");
        cliente.setCorreo("ana@example.com");
        cliente.setRedWan(red);
        return cliente;
    }
}
