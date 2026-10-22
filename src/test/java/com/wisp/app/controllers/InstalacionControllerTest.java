package com.wisp.app.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.wisp.app.dto.InstalacionDto;
import com.wisp.app.entity.Cliente;
import com.wisp.app.entity.Instalacion;
import com.wisp.app.repository.InstalacionRepository;
import com.wisp.app.servicios.ClienteService;

@ExtendWith(MockitoExtension.class)
class InstalacionControllerTest {

    @Mock
    private InstalacionRepository instalacionRepository;

    @Mock
    private ClienteService clienteService;

    private InstalacionController controller;

    @BeforeEach
    void setUp() {
        controller = new InstalacionController(instalacionRepository, clienteService);
    }

    @Test
    void nuevaCargaSoloClientesNuevos() {
        Cliente cliente = cliente(1L, "Nuevo", "Av. Siempre 123");
        when(clienteService.buscarPorEstado("Nuevo")).thenReturn(List.of(cliente));
        Model model = new ExtendedModelMap();

        String view = controller.nueva(model);

        assertThat(view).isEqualTo("instalaciones-form");
        assertThat(model.getAttribute("instalacion")).isInstanceOf(InstalacionDto.class);
        assertThat((List<?>) model.getAttribute("clientes")).hasSize(1);
        assertThat(((List<?>) model.getAttribute("clientes")).get(0)).isSameAs(cliente);
    }

    @Test
    void listarSinFiltroDevuelveTodas() {
        Instalacion instalacion = instalacion("Pendiente");
        when(instalacionRepository.findAll()).thenReturn(List.of(instalacion));
        Model model = new ExtendedModelMap();

        controller.listar(model, null);

        assertThat((List<?>) model.getAttribute("instalaciones")).hasSize(1);
        assertThat(((List<?>) model.getAttribute("instalaciones")).get(0)).isSameAs(instalacion);
        assertThat(model.getAttribute("estadoSeleccionado")).isNull();
    }

    @Test
    void listarConEstadoFiltra() {
        Instalacion instalacion = instalacion("Atendido");
        when(instalacionRepository.findByEstado("Atendido")).thenReturn(List.of(instalacion));
        Model model = new ExtendedModelMap();

        controller.listar(model, "Atendido");

        assertThat((List<?>) model.getAttribute("instalaciones")).hasSize(1);
        assertThat(((List<?>) model.getAttribute("instalaciones")).get(0)).isSameAs(instalacion);
        assertThat(model.getAttribute("estadoSeleccionado")).isEqualTo("Atendido");
    }

    @Test
    void nuevaInstalacionQuedaPendienteConDireccionDelCliente() {
        Cliente cliente = cliente(5L, "Nuevo", "Av. Siempre 123");
        when(clienteService.obtenerClientePorId(5L)).thenReturn(cliente);
        InstalacionDto dto = new InstalacionDto();
        dto.setTipo("Nueva");
        dto.setClienteId(5L);
        dto.setEstado("Atendido");
        dto.setCobrarInstalacion(true);
        dto.setMontoInstalacion(150.0);
        dto.setMetodoPago("Efectivo");
        dto.setObservacion("Instalar router");

        String view = controller.guardar(dto);

        ArgumentCaptor<Instalacion> captor = ArgumentCaptor.forClass(Instalacion.class);
        verify(instalacionRepository).save(captor.capture());
        Instalacion guardada = captor.getValue();
        assertThat(view).isEqualTo("redirect:/instalaciones");
        assertThat(guardada.getEstado()).isEqualTo("Pendiente");
        assertThat(guardada.getFechaRegistro()).isNotNull();
        assertThat(guardada.getDireccion()).isEqualTo("Av. Siempre 123");
        assertThat(guardada.getPreCliente()).isSameAs(cliente);
        assertThat(guardada.getMontoInstalacion()).isEqualTo(150.0);
    }

    @Test
    void noEditaInstalacionQueNoEstaPendiente() {
        Instalacion existente = instalacion("Atendido");
        when(instalacionRepository.findById(1L)).thenReturn(Optional.of(existente));
        InstalacionDto dto = new InstalacionDto();
        dto.setId(1L);
        dto.setEstado("Atendido");

        String view = controller.guardar(dto);

        assertThat(view).isEqualTo("redirect:/instalaciones");
        verify(instalacionRepository, never()).save(any());
    }

    @Test
    void rechazaEstadoNoPermitidoAlEditar() {
        Instalacion existente = instalacion("Pendiente");
        when(instalacionRepository.findById(1L)).thenReturn(Optional.of(existente));
        InstalacionDto dto = new InstalacionDto();
        dto.setId(1L);
        dto.setEstado("En proceso");

        String view = controller.guardar(dto);

        assertThat(view).isEqualTo("redirect:/instalaciones");
        verify(instalacionRepository, never()).save(any());
    }

    @Test
    void alPasarAAtendidoAsignaFechaYActivaCliente() {
        Cliente cliente = cliente(5L, "Nuevo", "Av. Siempre 123");
        Instalacion existente = instalacion("Pendiente");
        existente.setPreCliente(cliente);
        when(instalacionRepository.findById(1L)).thenReturn(Optional.of(existente));
        InstalacionDto dto = new InstalacionDto();
        dto.setId(1L);
        dto.setEstado("Atendido");

        controller.guardar(dto);

        assertThat(existente.getEstado()).isEqualTo("Atendido");
        assertThat(existente.getFechaAtencion()).isNotNull();
        assertThat(cliente.getEstado()).isEqualTo("Activo");
        verify(clienteService).guardarCliente(cliente);
    }

    @Test
    void noActivaClienteQueYaEstaActivo() {
        Cliente cliente = cliente(5L, "Activo", "Av. Siempre 123");
        Instalacion existente = instalacion("Pendiente");
        existente.setPreCliente(cliente);
        when(instalacionRepository.findById(1L)).thenReturn(Optional.of(existente));
        InstalacionDto dto = new InstalacionDto();
        dto.setId(1L);
        dto.setEstado("Atendido");

        controller.guardar(dto);

        verify(clienteService, never()).guardarCliente(any());
    }

    @Test
    void noSobrescribeFechaDeAtencionExistente() {
        LocalDateTime fecha = LocalDateTime.of(2026, 1, 1, 10, 0);
        Instalacion existente = instalacion("Pendiente");
        existente.setFechaAtencion(fecha);
        when(instalacionRepository.findById(1L)).thenReturn(Optional.of(existente));
        InstalacionDto dto = new InstalacionDto();
        dto.setId(1L);
        dto.setEstado("Atendido");

        controller.guardar(dto);

        assertThat(existente.getFechaAtencion()).isEqualTo(fecha);
    }

    @Test
    void editarBloqueaInstalacionNoPendiente() {
        Instalacion existente = instalacion("Atendido");
        when(instalacionRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteService.listarClientes()).thenReturn(List.of());
        Model model = new ExtendedModelMap();

        String view = controller.editar(1L, model);

        assertThat(view).isEqualTo("redirect:/instalaciones");
    }

    @Test
    void editarPendienteDevuelveFormularioConNombreCliente() {
        Cliente cliente = cliente(5L, "Nuevo", "Av. Siempre 123");
        Instalacion existente = instalacion("Pendiente");
        existente.setPreCliente(cliente);
        when(instalacionRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteService.listarClientes()).thenReturn(List.of(cliente));
        Model model = new ExtendedModelMap();

        String view = controller.editar(1L, model);

        InstalacionDto dto = (InstalacionDto) model.getAttribute("instalacion");
        assertThat(view).isEqualTo("instalaciones-form");
        assertThat(dto.getNombreCliente()).isEqualTo("Ana Perez");
        assertThat((List<?>) model.getAttribute("clientes")).hasSize(1);
        assertThat(((List<?>) model.getAttribute("clientes")).get(0)).isSameAs(cliente);
    }

    private Instalacion instalacion(String estado) {
        Instalacion instalacion = new Instalacion();
        instalacion.setId(1L);
        instalacion.setEstado(estado);
        return instalacion;
    }

    private Cliente cliente(Long id, String estado, String direccion) {
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setNombres("Ana");
        cliente.setApellidos("Perez");
        cliente.setEstado(estado);
        cliente.setDireccion(direccion);
        return cliente;
    }
}
