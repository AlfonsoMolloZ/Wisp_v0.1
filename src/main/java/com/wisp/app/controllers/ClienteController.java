package com.wisp.app.controllers;

import com.wisp.app.entity.Planes;
import com.wisp.app.entity.RedWan;
import com.wisp.app.dto.ClienteDto;
import com.wisp.app.entity.Cliente;
import com.wisp.app.servicios.ClienteService;
import com.wisp.app.servicios.PagosService;
import com.wisp.app.servicios.PlanesService;
import com.wisp.app.servicios.RedWanService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final PlanesService planesService;
    private final PagosService pagosService;
    private final RedWanService redWanService;

    public ClienteController(
            ClienteService clienteService,
            PlanesService planesService,
            PagosService pagosService,
            RedWanService redWanService) {

        this.clienteService = clienteService;
        this.planesService = planesService;
        this.pagosService = pagosService;
        this.redWanService = redWanService;
    }

    @GetMapping
    public String listar(Model model, @RequestParam(required = false) String estado) {
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("estadoSeleccionado", estado);

        // validacion de estado para filtrar clientes por estado
        if (estado == null || estado.isBlank()) {
            model.addAttribute("clientes", clienteService.listarClientes());
        } else {
            model.addAttribute("clientes", clienteService.buscarPorEstado(estado));
        }

        return "clientes";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new ClienteDto());
        model.addAttribute("planes", planesService.listarTodos());
        model.addAttribute("redes", redWanService.listarTodas());
        return "clientes-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute ClienteDto clienteDto, Model model) {

        // maneja la excepcion de correo y dni duplicado
        try {
            Cliente cliente = toEntity(clienteDto);
            clienteService.guardarCliente(cliente);
            return "redirect:/clientes";

        } catch (RuntimeException e) {

            model.addAttribute("error", e.getMessage());
            model.addAttribute("cliente", clienteDto);
            model.addAttribute("planes", planesService.listarTodos());
            model.addAttribute("redes", redWanService.listarTodas());
            return "clientes-form";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        Cliente cliente = clienteService.obtenerClientePorId(id);

        model.addAttribute("cliente", toDto(cliente));
        model.addAttribute("planes", planesService.listarTodos());
        model.addAttribute("redes", redWanService.listarTodas());

        return "clientes-form";
    }

    @PostMapping("/suspender/{id}")
    public String suspender(@PathVariable Long id) {

        // clienteService.eliminarCliente(id);
        clienteService.suspenderCliente(id);

        return "redirect:/clientes";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {

        model.addAttribute(
                "cliente",
                clienteService.obtenerClientePorId(id));

        return "clientesInfo";
    }

    // para ver el historial de pagos de un cliente
    @GetMapping("/{id}/pagos")
    public String historialPagos(
            @PathVariable Long id,
            Model model) {

        Cliente cliente = clienteService.obtenerClientePorId(id);

        model.addAttribute("cliente", cliente);
        model.addAttribute("pagos", pagosService.buscarPorCliente(id));

        return "clientes-pagos";
    }

    private Cliente toEntity(ClienteDto dto) {

        if (dto == null) {
            return null;
        }

        Cliente cliente = new Cliente();

        cliente.setId(dto.getId());
        cliente.setNombres(dto.getNombres());
        cliente.setApellidos(dto.getApellidos());
        cliente.setDni(dto.getDni());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setCorreo(dto.getCorreo());
        cliente.setEstado(dto.getEstado());

        if (dto.getPlanId() != null) {

            Planes plan = planesService.buscarPorId(dto.getPlanId());
            cliente.setPlan(plan);
        }

        if (dto.getRedWanId() != null) {

            RedWan red = redWanService.buscarPorId(dto.getRedWanId());

            cliente.setRedWan(red);
        }

        return cliente;
    }

    private ClienteDto toDto(Cliente cliente) {

        if (cliente == null) {
            return new ClienteDto();
        }

        ClienteDto dto = new ClienteDto();

        dto.setId(cliente.getId());
        dto.setNombres(cliente.getNombres());
        dto.setApellidos(cliente.getApellidos());
        dto.setDni(cliente.getDni());
        dto.setTelefono(cliente.getTelefono());
        dto.setDireccion(cliente.getDireccion());
        dto.setCorreo(cliente.getCorreo());
        dto.setEstado(cliente.getEstado());
        dto.setIp(cliente.getIp());

        if (cliente.getPlan() != null) {
            dto.setPlanId(cliente.getPlan().getId());
        }
        if (cliente.getRedWan() != null) {
            dto.setRedWanId(cliente.getRedWan().getId());
        }

        return dto;
    }
}