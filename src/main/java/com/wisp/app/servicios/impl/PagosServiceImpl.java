package com.wisp.app.servicios.impl;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.wisp.app.entity.Cliente;
import com.wisp.app.entity.Pagos;
import com.wisp.app.repository.ClienteRepository;
import com.wisp.app.repository.PagosRepository;
import com.wisp.app.servicios.PagosService;

@Service
public class PagosServiceImpl implements PagosService {

    private static final Logger log = LoggerFactory.getLogger(PagosServiceImpl.class);

    private final ClienteRepository clienteRepository;
    private final PagosRepository pagosRepository;

    public PagosServiceImpl(
            ClienteRepository clienteRepository,
            PagosRepository pagosRepository) {

        this.clienteRepository = clienteRepository;
        this.pagosRepository = pagosRepository;
    }

    @Override
    public void generarPagosMensuales() {
        LocalDate hoy = LocalDate.now();
        int mesActual = hoy.getMonthValue();
        int anioActual = hoy.getYear();

        List<Cliente> clientes = clienteRepository.findAll();

        for (Cliente cliente : clientes) {

            if (!"Activo".equals(cliente.getEstado())) {
                continue;
            }

            if (cliente.getPlan() == null || cliente.getPlan().getPrecio() == null) {
                log.warn("Cliente {} sin plan o precio: no se genero el pago mensual.", cliente.getId());
                continue;
            }

            boolean existe = pagosRepository.existsByClienteIdAndMesAndAnio(
                    cliente.getId(),
                    mesActual,
                    anioActual);

            if (existe) {
                continue;
            }

            Pagos pago = new Pagos();

            pago.setCliente(cliente);
            pago.setMonto(cliente.getPlan().getPrecio());

            pago.setMes(mesActual);
            pago.setAnio(anioActual);

            pago.setFechaEmision(hoy);
            pago.setFechaVencimiento(hoy.withDayOfMonth(1).plusMonths(1));

            pago.setEstado("Pendiente");

            pagosRepository.save(pago);
        }
    }

    @Override
    public List<Pagos> listarTodos() {
        return pagosRepository.findAll();
    }

    @Override
    public Pagos buscarPorId(Long id) {
        return pagosRepository.findById(id).orElseThrow();
    }

    @Override
    public List<Pagos> buscarPorCliente(Long clienteId) {
        return pagosRepository.findByClienteId(clienteId);
    }

    @Override
    public Pagos guardar(Pagos pago) {
        if (pago.getId() == null) {
            throw new IllegalArgumentException("El pago no existe.");
        }

        Pagos original = pagosRepository.findById(pago.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado."));

        String estadoAnterior = original.getEstado();
        String estadoNuevo = pago.getEstado();

        // valiacion de regla de negocio pare del estado pendiente solo pueda pasar a
        // pagodo y de pagado a anulado
        if ("Pagado".equals(estadoAnterior)) {

            if (!"Pagado".equals(estadoNuevo)
                    && !"Anulado".equals(estadoNuevo)) {

                throw new RuntimeException(
                        "Un pago realizado solo puede anularse.");
            }

        } else {

            if (!estadoAnterior.equals(estadoNuevo)
                    && !"Pagado".equals(estadoNuevo)) {

                throw new RuntimeException(
                        "Solo puede cambiar el estado a Pagado.");
            }

        }

        original.setMonto(pago.getMonto());
        original.setFechaEmision(pago.getFechaEmision());
        original.setFechaVencimiento(pago.getFechaVencimiento());
        original.setFechaPago(pago.getFechaPago());
        original.setEstado(pago.getEstado());
        original.setMetodoPago(pago.getMetodoPago());
        original.setObservacion(pago.getObservacion());

        return pagosRepository.save(original);
    }

    // metodo para actualizar el estado de los pagos vencidos cada vez q sea habra
    // el modulo de pagos
    @Override
    public void actualizarPagosVencidos() {

        List<Pagos> pagos = pagosRepository.findAll();

        for (Pagos pago : pagos) {

            if ("Pendiente".equals(pago.getEstado())
                    && pago.getFechaVencimiento() != null
                    && LocalDate.now().isAfter(pago.getFechaVencimiento())) {

                pago.setEstado("Vencido");
                pagosRepository.save(pago);
            }
        }

    }
}
