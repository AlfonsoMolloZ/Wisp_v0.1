package com.wisp.app.servicios.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.wisp.app.entity.Cliente;
import com.wisp.app.entity.RedWan;
import com.wisp.app.repository.ClienteRepository;
import com.wisp.app.servicios.ClienteService;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente guardarCliente(Cliente cliente) {

        if (cliente.getId() == null) {

            // Nuevo cliente
            cliente.setEstado("Nuevo");

        } else {

            Cliente original = clienteRepository.findById(cliente.getId())
                    .orElseThrow();

            if ("Suspendido".equals(original.getEstado())) {

                // Solo puede permanecer suspendido o volver a Activo
                if (!"Activo".equals(cliente.getEstado())
                        && !"Suspendido".equals(cliente.getEstado())) {

                    throw new RuntimeException(
                            "Solo se puede reactivar un cliente suspendido.");
                }

            } else {

                // Nuevo y Activo no pueden cambiar su estado desde este formulario
                cliente.setEstado(original.getEstado());
            }
        }

        // validacion de correo
        if (clienteRepository.existsByCorreo(cliente.getCorreo())) {

            // Si es nuevo cliente
            if (cliente.getId() == null) {
                throw new RuntimeException("El correo ya existe");
            }

            // valiadacion del cooreo en la edicion
            Cliente clienteBD = clienteRepository.findById(cliente.getId()).orElse(null);

            if (clienteBD != null &&
                    !clienteBD.getCorreo().equals(cliente.getCorreo())) {

                throw new RuntimeException("El correo ya existe");
            }
        }

        // valiadcion del dni
        if (clienteRepository.existsByDni(cliente.getDni())) {

            if (cliente.getId() == null) {
                throw new RuntimeException("El DNI ya existe");
            }

            Cliente clienteBD = clienteRepository.findById(cliente.getId()).orElse(null);

            // validacion cuando del dmni cuando se edita
            if (clienteBD != null &&
                    !clienteBD.getDni().equals(cliente.getDni())) {

                throw new RuntimeException("El DNI ya existe");
            }
        }

        // validacion de la ip, si es nula o vacia se genera una ip disponible
        if (cliente.getIp() == null || cliente.getIp().isEmpty()) {

            String ip = generarIpDisponible(cliente.getRedWan());

            cliente.setIp(ip);
        }

        // validacion de la fecha de suspension, si el estado es activo se pone en null,
        // asi la fecha solo existirá cuando realmente esté suspendido.
        if ("Activo".equals(cliente.getEstado())) {
            cliente.setFechaSuspension(null);
        }

        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente obtenerClientePorId(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    // @Override
    // public void eliminarCliente(Long id) {
    // clienteRepository.deleteById(id);
    // }

    @Override
    public long totalClientes() {
        return clienteRepository.count();
    }

    // BUSCAR POR ESTADO
    @Override
    public List<Cliente> buscarPorEstado(String estado) {
        return clienteRepository.findByEstado(estado);
    }

    // metodo para generar una ip disponible
    private String generarIpDisponible(RedWan redWan) {

        List<Cliente> clientes = clienteRepository.findByRedWanId(redWan.getId());

        Set<String> ipsUsadas = clientes.stream()
                .map(Cliente::getIp)
                .collect(Collectors.toSet());

        String base = redWan.getDireccionRed()
                .substring(0, redWan.getDireccionRed().lastIndexOf("."));

        int limite = calcularCantidadHosts(redWan.getMascara());

        for (int i = 2; i < limite; i++) {

            String ip = base + "." + i;

            if (!ipsUsadas.contains(ip)) {
                return ip;
            }
        }

        throw new RuntimeException("No hay IP disponibles");
    }

    // metodo para calcular el limite de hosts en una red dado su mascara
    private int calcularCantidadHosts(String mascara) {

        String[] partes = mascara.split("\\.");

        int bits = 0;

        for (String parte : partes) {

            int numero = Integer.parseInt(parte);

            while (numero > 0) {
                bits += numero & 1;
                numero >>= 1;
            }
        }

        return (int) Math.pow(2, 32 - bits) - 2;
    }

    @Override
    public void suspenderCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow();

        cliente.setEstado("Suspendido");
        cliente.setFechaSuspension(LocalDateTime.now());

        clienteRepository.save(cliente);
    }
}