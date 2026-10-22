package com.wisp.app.servicios.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wisp.app.entity.Cliente;
import com.wisp.app.entity.Pagos;
import com.wisp.app.entity.Planes;
import com.wisp.app.repository.ClienteRepository;
import com.wisp.app.repository.PagosRepository;

@ExtendWith(MockitoExtension.class)
class PagosServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PagosRepository pagosRepository;

    private PagosServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PagosServiceImpl(clienteRepository, pagosRepository);
    }

    @Test
    void generaPagoPendienteConVencimientoElPrimerDiaDelSiguienteMes() {
        when(clienteRepository.findAll()).thenReturn(List.of(cliente(1L, "Activo", 50.0)));
        when(pagosRepository.existsByClienteIdAndMesAndAnio(eq(1L), anyInt(), anyInt())).thenReturn(false);

        service.generarPagosMensuales();

        ArgumentCaptor<Pagos> captor = ArgumentCaptor.forClass(Pagos.class);
        verify(pagosRepository).save(captor.capture());
        Pagos pago = captor.getValue();

        assertThat(pago.getMonto()).isEqualTo(50.0);
        assertThat(pago.getEstado()).isEqualTo("Pendiente");
        assertThat(pago.getFechaVencimiento())
                .isEqualTo(LocalDate.now().withDayOfMonth(1).plusMonths(1));
    }

    @Test
    void omiteClienteSinPlanAlGenerarPagos() {
        when(clienteRepository.findAll()).thenReturn(List.of(cliente(2L, "Activo", null)));

        service.generarPagosMensuales();

        verify(pagosRepository, never()).save(any(Pagos.class));
    }

    @Test
    void omiteClientesNoActivos() {
        when(clienteRepository.findAll()).thenReturn(List.of(cliente(3L, "Suspendido", 50.0)));

        service.generarPagosMensuales();

        verify(pagosRepository, never()).save(any(Pagos.class));
    }

    @Test
    void omitePagoSiYaExisteElDelMes() {
        when(clienteRepository.findAll()).thenReturn(List.of(cliente(4L, "Activo", 50.0)));
        when(pagosRepository.existsByClienteIdAndMesAndAnio(eq(4L), anyInt(), anyInt())).thenReturn(true);

        service.generarPagosMensuales();

        verify(pagosRepository, never()).save(any(Pagos.class));
    }

    @Test
    void omiteClienteConPrecioNulo() {
        Cliente cliente = cliente(5L, "Activo", 50.0);
        cliente.getPlan().setPrecio(null);
        when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        service.generarPagosMensuales();

        verify(pagosRepository, never()).save(any(Pagos.class));
    }

    @Test
    void rechazaGuardarPagoSinId() {
        Pagos pago = pago(null, "Pendiente");

        assertThatThrownBy(() -> service.guardar(pago))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El pago no existe.");

        verifyNoInteractions(pagosRepository);
    }

    @Test
    void rechazaGuardarPagoInexistente() {
        Pagos pago = pago(99L, "Pendiente");
        when(pagosRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.guardar(pago))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Pago no encontrado.");
    }

    @ParameterizedTest(name = "{0} -> {1} permitido={2}")
    @CsvSource({
            "Pendiente, Pagado, true",
            "Pendiente, Pendiente, true",
            "Pendiente, Anulado, false",
            "Pendiente, Vencido, false",
            "Pagado, Pagado, true",
            "Pagado, Anulado, true",
            "Pagado, Vencido, false",
            "Vencido, Pagado, true",
            "Vencido, Vencido, true",
            "Vencido, Anulado, false"
    })
    void validaTransicionesDeEstado(String estadoAnterior, String estadoNuevo, boolean permitido) {
        Pagos original = pago(1L, estadoAnterior);
        Pagos cambio = pago(1L, estadoNuevo);
        when(pagosRepository.findById(1L)).thenReturn(Optional.of(original));

        if (permitido) {
            when(pagosRepository.save(original)).thenReturn(original);
            assertThat(service.guardar(cambio).getEstado()).isEqualTo(estadoNuevo);
        } else {
            assertThatThrownBy(() -> service.guardar(cambio))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Test
    void marcaVencidosSinFallarCuandoNoHayFecha() {
        Pagos vencido = pago(1L, "Pendiente");
        vencido.setFechaVencimiento(LocalDate.now().minusDays(1));
        Pagos sinFecha = pago(2L, "Pendiente");
        when(pagosRepository.findAll()).thenReturn(List.of(vencido, sinFecha));

        service.actualizarPagosVencidos();

        assertThat(vencido.getEstado()).isEqualTo("Vencido");
        assertThat(sinFecha.getEstado()).isEqualTo("Pendiente");
        verify(pagosRepository).save(vencido);
    }

    @Test
    void ignoraPagosPagadosAlActualizarVencidos() {
        Pagos pagado = pago(3L, "Pagado");
        pagado.setFechaVencimiento(LocalDate.now().minusDays(1));
        when(pagosRepository.findAll()).thenReturn(List.of(pagado));

        service.actualizarPagosVencidos();

        assertThat(pagado.getEstado()).isEqualTo("Pagado");
        verify(pagosRepository, never()).save(any(Pagos.class));
    }

    private Cliente cliente(Long id, String estado, Double precio) {
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setEstado(estado);
        if (precio != null) {
            Planes plan = new Planes();
            plan.setId(10L);
            plan.setNombre("Básico");
            plan.setPrecio(precio);
            plan.setVelocidad(10);
            cliente.setPlan(plan);
        }
        return cliente;
    }

    private Pagos pago(Long id, String estado) {
        Pagos pago = new Pagos();
        pago.setId(id);
        pago.setEstado(estado);
        pago.setMonto(50.0);
        return pago;
    }
}
