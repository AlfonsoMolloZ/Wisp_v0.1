package com.wisp.app.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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

import com.wisp.app.dto.AveriaDto;
import com.wisp.app.entity.Averia;
import com.wisp.app.entity.Cliente;
import com.wisp.app.repository.AveriaRepository;
import com.wisp.app.servicios.ClienteService;

@ExtendWith(MockitoExtension.class)
class AveriaControllerTest {

    @Mock
    private AveriaRepository averiaRepository;

    @Mock
    private ClienteService clienteService;

    private AveriaController controller;

    @BeforeEach
    void setUp() {
        controller = new AveriaController(averiaRepository, clienteService);
    }

    @Test
    void nuevaCargaClientes() {
        Cliente cliente = cliente(1L);
        when(clienteService.listarClientes()).thenReturn(List.of(cliente));
        Model model = new ExtendedModelMap();

        String view = controller.nueva(model);

        assertThat(view).isEqualTo("averias-form");
        assertThat(model.getAttribute("averia")).isInstanceOf(AveriaDto.class);
        assertThat((List<?>) model.getAttribute("clientes")).hasSize(1);
        assertThat(((List<?>) model.getAttribute("clientes")).get(0)).isSameAs(cliente);
    }

    @Test
    void listarDevuelveTodasLasAverias() {
        Averia averia = averia("Pendiente");
        when(averiaRepository.findAll()).thenReturn(List.of(averia));
        Model model = new ExtendedModelMap();

        controller.listar(model);

        assertThat((List<?>) model.getAttribute("averias")).hasSize(1);
        assertThat(((List<?>) model.getAttribute("averias")).get(0)).isSameAs(averia);
    }

    @Test
    void nuevaAveriaQuedaSinAtenderConFechaDeRegistro() {
        Cliente cliente = cliente(5L);
        when(clienteService.obtenerClientePorId(5L)).thenReturn(cliente);
        AveriaDto dto = dto(null, "AV-1", null);
        dto.setClienteId(5L);

        controller.guardar(dto);

        ArgumentCaptor<Averia> captor = ArgumentCaptor.forClass(Averia.class);
        verify(averiaRepository).save(captor.capture());
        Averia guardada = captor.getValue();
        assertThat(guardada.getEstado()).isEqualTo("Sin atender");
        assertThat(guardada.getFechaRegistro()).isEqualTo(LocalDate.now());
        assertThat(guardada.getCliente()).isSameAs(cliente);
    }

    @Test
    void respetaEstadoExplicitoAlCrear() {
        Cliente cliente = cliente(5L);
        when(clienteService.obtenerClientePorId(5L)).thenReturn(cliente);
        AveriaDto dto = dto(null, "AV-2", "En proceso");
        dto.setClienteId(5L);

        controller.guardar(dto);

        ArgumentCaptor<Averia> captor = ArgumentCaptor.forClass(Averia.class);
        verify(averiaRepository).save(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo("En proceso");
    }

    @Test
    void editarAAtendidoAsignaFechaDeAtencion() {
        Averia existente = averia("Pendiente");
        existente.setFechaAtencion(null);
        when(averiaRepository.findById(1L)).thenReturn(Optional.of(existente));
        AveriaDto dto = dto(1L, "AV-1", "Atendido");

        controller.guardar(dto);

        assertThat(existente.getEstado()).isEqualTo("Atendido");
        assertThat(existente.getFechaAtencion()).isEqualTo(LocalDate.now());
    }

    @Test
    void noSobrescribeFechaDeAtencionExistente() {
        LocalDate fecha = LocalDate.of(2026, 1, 1);
        Averia existente = averia("Pendiente");
        existente.setFechaAtencion(fecha);
        when(averiaRepository.findById(1L)).thenReturn(Optional.of(existente));
        AveriaDto dto = dto(1L, "AV-1", "Atendido");

        controller.guardar(dto);

        assertThat(existente.getFechaAtencion()).isEqualTo(fecha);
    }

    @Test
    void editarMapeaNombreDelCliente() {
        Averia existente = averia("Pendiente");
        existente.setCliente(cliente(5L));
        when(averiaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteService.listarClientes()).thenReturn(List.of());
        Model model = new ExtendedModelMap();

        String view = controller.editar(1L, model);

        AveriaDto dto = (AveriaDto) model.getAttribute("averia");
        assertThat(view).isEqualTo("averias-form");
        assertThat(dto.getNombreCliente()).isEqualTo("Ana Perez");
    }

    private Averia averia(String estado) {
        Averia averia = new Averia();
        averia.setId(1L);
        averia.setCodigo("AV-1");
        averia.setDescripcion("Sin internet");
        averia.setDireccion("Calle 1");
        averia.setEstado(estado);
        return averia;
    }

    private AveriaDto dto(Long id, String codigo, String estado) {
        AveriaDto dto = new AveriaDto();
        dto.setId(id);
        dto.setCodigo(codigo);
        dto.setDescripcion("Sin internet");
        dto.setDireccion("Calle 1");
        dto.setEstado(estado);
        return dto;
    }

    private Cliente cliente(Long id) {
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setNombres("Ana");
        cliente.setApellidos("Perez");
        return cliente;
    }
}
