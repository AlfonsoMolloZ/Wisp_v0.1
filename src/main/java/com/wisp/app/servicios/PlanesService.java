package com.wisp.app.servicios;

import java.util.List;
import com.wisp.app.entity.Planes;

public interface PlanesService {

    List<Planes> listarTodos();

    Planes buscarPorId(Long id);

    Planes guardar(Planes plan);

    void eliminar(Long id);
}