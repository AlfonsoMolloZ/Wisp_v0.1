package com.wisp.app.servicios.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wisp.app.entity.Planes;
import com.wisp.app.repository.ClienteRepository;
import com.wisp.app.repository.PlanesRepository;
import com.wisp.app.servicios.PlanesService;

@Service
public class PlanesServiceImpl implements PlanesService {

    private final PlanesRepository repository;
    private final ClienteRepository clienteRepository;

    public PlanesServiceImpl(PlanesRepository repository, ClienteRepository clienteRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<Planes> listarTodos() {
        return repository.findAll();
    }

    @Override
    public Planes buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Planes guardar(Planes plan) {
        return repository.save(plan);
    }

    @Override
    public void eliminar(Long id) {
        if (clienteRepository.existsByPlanId(id)) {
            throw new IllegalStateException("No se puede eliminar el plan porque tiene clientes asignados.");
        }
        repository.deleteById(id);
    }
}