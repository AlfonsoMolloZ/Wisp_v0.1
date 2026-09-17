package com.wisp.app.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wisp.app.NegocioException;
import com.wisp.app.entity.Cliente;
import com.wisp.app.entity.Pagos;
import com.wisp.app.entity.Planes;
import com.wisp.app.repository.ClienteRepository;
import com.wisp.app.repository.PagosRepository;
import com.wisp.app.servicios.impl.PagosServiceImpl;

@ExtendWith(MockitoExtension.class)
class PagosServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private PagosRepository pagosRepository;

    private PagosServiceImpl servicio;

    @BeforeEach
    void setUp() {
        servicio = new PagosServiceImpl(clienteRepository, pagosRepository);
    }

    private Planes plan(Double precio) {
        Planes p = new Planes();
        p.setPrecio(precio);
        return p;
    }

    private Cliente cliente(Long id, String estado, Planes plan) {
        Cliente c = new Cliente();
        c.setId(id);
        c.setEstado(estado);
        c.setPlan(plan);
        return c;
    }

    private Pagos pagoConEstado(Long id, String estado, LocalDate vencimiento) {
        Pagos p = new Pagos();
        p.setId(id);
        p.setEstado(estado);
        p.setFechaVencimiento(vencimiento);
        return p;
    }

    @Test
    void generaPagoSoloParaClientesActivos() {
        Cliente activo = cliente(1L, "Activo", plan(100.0));
        Cliente suspendido = cliente(2L, "Suspendido", plan(100.0));
        when(clienteRepository.findAll()).thenReturn(Arrays.asList(activo, suspendido));
        when(pagosRepository.existsByClienteIdAndMesAndAnio(eq(1L), anyInt(), anyInt())).thenReturn(false);

        servicio.generarPagosMensuales();

        ArgumentCaptor<Pagos> captor = ArgumentCaptor.forClass(Pagos.class);
        verify(pagosRepository, times(1)).save(captor.capture());
        Pagos pago = captor.getValue();
        assertEquals(activo, pago.getCliente());
        assertEquals(100.0, pago.getMonto());
        assertEquals("Pendiente", pago.getEstado());
        assertEquals(LocalDate.now().plusDays(30), pago.getFechaVencimiento());
    }

    @Test
    void noGeneraPagoSiYaExisteParaElMes() {
        Cliente activo = cliente(1L, "Activo", plan(100.0));
        when(clienteRepository.findAll()).thenReturn(List.of(activo));
        when(pagosRepository.existsByClienteIdAndMesAndAnio(eq(1L), anyInt(), anyInt())).thenReturn(true);

        servicio.generarPagosMensuales();

        verify(pagosRepository, never()).save(any(Pagos.class));
    }

    @Test
    void clienteSinPlanNoRompeLaGeneracion() {
        Cliente sinPlan = cliente(1L, "Activo", null);
        when(clienteRepository.findAll()).thenReturn(List.of(sinPlan));

        servicio.generarPagosMensuales();

        verify(pagosRepository, never()).save(any(Pagos.class));
    }

    @Test
    void guardarPendienteAPagadoEsValido() {
        Pagos original = pagoConEstado(1L, "Pendiente", null);
        when(pagosRepository.findById(1L)).thenReturn(Optional.of(original));
        when(pagosRepository.save(original)).thenReturn(original);

        Pagos cambios = pagoConEstado(1L, "Pagado", null);

        Pagos resultado = servicio.guardar(cambios);

        assertEquals("Pagado", resultado.getEstado());
        verify(pagosRepository).save(original);
    }

    @Test
    void guardarPagadoNoPuedeVolverAPendiente() {
        Pagos original = pagoConEstado(1L, "Pagado", null);
        when(pagosRepository.findById(1L)).thenReturn(Optional.of(original));

        Pagos cambios = pagoConEstado(1L, "Pendiente", null);

        assertThrows(NegocioException.class, () -> servicio.guardar(cambios));
    }

    @Test
    void guardarPagadoSoloPuedeAnularse() {
        Pagos original = pagoConEstado(1L, "Pagado", null);
        when(pagosRepository.findById(1L)).thenReturn(Optional.of(original));

        Pagos cambios = pagoConEstado(1L, "Vencido", null);

        assertThrows(NegocioException.class, () -> servicio.guardar(cambios));
    }

    @Test
    void guardarPendienteSoloPuedePasarAPagado() {
        Pagos original = pagoConEstado(1L, "Pendiente", null);
        when(pagosRepository.findById(1L)).thenReturn(Optional.of(original));

        Pagos cambios = pagoConEstado(1L, "Anulado", null);

        assertThrows(NegocioException.class, () -> servicio.guardar(cambios));
    }

    @Test
    void actualizaSoloPagosVencidos() {
        Pagos vencido = pagoConEstado(1L, "Pendiente", LocalDate.now().minusDays(1));
        Pagos vigente = pagoConEstado(2L, "Pendiente", LocalDate.now().plusDays(5));
        Pagos pagado = pagoConEstado(3L, "Pagado", LocalDate.now().minusDays(3));
        when(pagosRepository.findAll()).thenReturn(Arrays.asList(vencido, vigente, pagado));

        servicio.actualizarPagosVencidos();

        assertEquals("Vencido", vencido.getEstado());
        assertEquals("Pendiente", vigente.getEstado());
        assertEquals("Pagado", pagado.getEstado());
        verify(pagosRepository, times(1)).save(vencido);
    }
}