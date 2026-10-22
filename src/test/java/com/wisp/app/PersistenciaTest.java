package com.wisp.app;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.wisp.app.entity.Cliente;
import com.wisp.app.entity.Planes;
import com.wisp.app.entity.RedWan;
import com.wisp.app.repository.ClienteRepository;
import com.wisp.app.repository.PlanesRepository;
import com.wisp.app.repository.RedWanRepository;

@SpringBootTest
@Transactional
class PersistenciaTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RedWanRepository redWanRepository;

    @Autowired
    private PlanesRepository planesRepository;

    @Test
    void detectaClientesAsignadosARedYPlan() {
        RedWan red = new RedWan();
        red.setNombre("Red principal");
        red.setDireccionRed("192.168.20.0");
        red.setMascara("255.255.255.0");
        red.setGateway("192.168.20.1");
        red.setEstado("Activa");
        red = redWanRepository.save(red);

        Planes plan = new Planes();
        plan.setNombre("Básico");
        plan.setPrecio(50.0);
        plan.setVelocidad(10);
        plan = planesRepository.save(plan);

        Cliente cliente = new Cliente();
        cliente.setNombres("Ana");
        cliente.setApellidos("Perez");
        cliente.setDni("87654321");
        cliente.setTelefono("999999999");
        cliente.setDireccion("Calle 1");
        cliente.setCorreo("ana.persistencia@example.com");
        cliente.setEstado("Activo");
        cliente.setPlan(plan);
        cliente.setRedWan(red);
        cliente.setIp("192.168.20.2");
        clienteRepository.saveAndFlush(cliente);

        assertThat(clienteRepository.existsByRedWanId(red.getId())).isTrue();
        assertThat(clienteRepository.existsByPlanId(plan.getId())).isTrue();
        assertThat(clienteRepository.existsByRedWanId(red.getId() + 1)).isFalse();
        assertThat(clienteRepository.existsByPlanId(plan.getId() + 1)).isFalse();
    }
}
