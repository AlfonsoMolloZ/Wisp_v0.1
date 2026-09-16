package com.wisp.app.servicios;

import java.util.List;

import com.wisp.app.entity.Pagos;

public interface PagosService {
    List<Pagos> listarTodos();

    Pagos buscarPorId(Long id);

    Pagos guardar(Pagos pago);
    
    List<Pagos> buscarPorCliente(Long clienteId);

    void generarPagosMensuales();
    
void actualizarPagosVencidos();
}
