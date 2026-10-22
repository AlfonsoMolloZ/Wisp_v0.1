package com.wisp.app.servicios.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Test
    void generaPrimeraIpLibreEnMascara29() {
        RedWan red = redWan();
        red.setMascara("255.255.255.248");
        Cliente existente = cliente(red);
        existente.setIp("192.168.10.2");

        when(clienteRepository.findByRedWanId(1L)).thenReturn(List.of(existente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        Cliente nuevo = cliente(red);
        service.guardarCliente(nuevo);

        assertThat(nuevo.getIp()).isEqualTo("192.168.10.3");
    }

    @Test
    void rechazaCorreoDuplicadoAlCrear() {
        Cliente nuevo = cliente(redWan());
        when(clienteRepository.existsByCorreo("ana@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.guardarCliente(nuevo))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El correo ya existe");
    }

    @Test
    void rechazaDniDuplicadoAlCrear() {
        Cliente nuevo = cliente(redWan());
        when(clienteRepository.existsByCorreo("ana@example.com")).thenReturn(false);
        when(clienteRepository.existsByDni("12345678")).thenReturn(true);

        assertThatThrownBy(() -> service.guardarCliente(nuevo))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El DNI ya existe");
    }

    @Test
    void permiteMismoCorreoYDniAlEditarElMismoCliente() {
        Cliente original = cliente(redWan());
        original.setId(1L);
        original.setEstado("Activo");
        Cliente cambios = cliente(redWan());
        cambios.setId(1L);
        cambios.setEstado("Activo");
        cambios.setIp("192.168.10.50");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(original));
        when(clienteRepository.existsByCorreo("ana@example.com")).thenReturn(true);
        when(clienteRepository.existsByDni("12345678")).thenReturn(true);
        when(clienteRepository.save(cambios)).thenReturn(cambios);

        service.guardarCliente(cambios);

        verify(clienteRepository).save(cambios);
    }

    @Test
    void permiteReactivarClienteSuspendido() {
        Cliente original = cliente(redWan());
        original.setId(1L);
        original.setEstado("Suspendido");
        original.setFechaSuspension(LocalDateTime.now());
        Cliente cambios = cliente(redWan());
        cambios.setId(1L);
        cambios.setEstado("Activo");
        cambios.setIp("192.168.10.51");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(original));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        service.guardarCliente(cambios);

        assertThat(cambios.getEstado()).isEqualTo("Activo");
        assertThat(cambios.getFechaSuspension()).isNull();
    }

    @Test
    void rechazaEstadoDistintoAlReactivarSuspendido() {
        Cliente original = cliente(redWan());
        original.setId(1L);
        original.setEstado("Suspendido");
        Cliente cambios = cliente(redWan());
        cambios.setId(1L);
        cambios.setEstado("Nuevo");
        cambios.setIp("192.168.10.52");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(original));

        assertThatThrownBy(() -> service.guardarCliente(cambios))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("reactivar");
    }

    @Test
    void conservaEstadoEnClienteNoSuspendido() {
        Cliente original = cliente(redWan());
        original.setId(1L);
        original.setEstado("Nuevo");
        Cliente cambios = cliente(redWan());
        cambios.setId(1L);
        cambios.setEstado("Activo");
        cambios.setIp("192.168.10.53");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(original));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        service.guardarCliente(cambios);

        assertThat(cambios.getEstado()).isEqualTo("Nuevo");
    }

    @Test
    void suspenderClienteAsignaEstadoYFecha() {
        Cliente original = cliente(redWan());
        original.setId(1L);
        original.setEstado("Activo");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(original));
        when(clienteRepository.save(original)).thenReturn(original);

        service.suspenderCliente(1L);

        assertThat(original.getEstado()).isEqualTo("Suspendido");
        assertThat(original.getFechaSuspension()).isNotNull();
    }

    @Test
    void lanzaErrorSiNoHayIpsDisponibles() {
        RedWan red = redWan();
        red.setMascara("255.255.255.252");

        when(clienteRepository.findByRedWanId(1L)).thenReturn(List.of());

        Cliente nuevo = cliente(red);

        assertThatThrownBy(() -> service.guardarCliente(nuevo))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No hay IP disponibles");
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
