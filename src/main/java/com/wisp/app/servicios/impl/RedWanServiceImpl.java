package com.wisp.app.servicios.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wisp.app.entity.RedWan;
import com.wisp.app.repository.ClienteRepository;
import com.wisp.app.repository.RedWanRepository;
import com.wisp.app.servicios.RedWanService;

@Service
public class RedWanServiceImpl implements RedWanService {

    private final RedWanRepository redWanRepository;
    private final ClienteRepository clienteRepository;
    
    public RedWanServiceImpl(RedWanRepository redWanRepository, ClienteRepository clienteRepository) {
        this.redWanRepository = redWanRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<RedWan> listarTodas() {
        return redWanRepository.findAll();
    }

    @Override
    public RedWan buscarPorId(Long id) {
        return redWanRepository.findById(id).orElseThrow();
    }

    @Override
    public RedWan guardar(RedWan red) {
        return redWanRepository.save(red);
    }

    @Override
    public void eliminar(Long id) {
        if (clienteRepository.existsByRedWanId(id)) {
            throw new IllegalStateException("No se puede eliminar la red WAN porque tiene clientes asignados.");
        }
        redWanRepository.deleteById(id);
    }


}
