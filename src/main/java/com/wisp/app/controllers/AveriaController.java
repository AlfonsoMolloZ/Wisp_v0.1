package com.wisp.app.controllers;

import com.wisp.app.dto.AveriaDto;
import com.wisp.app.entity.Averia;
import com.wisp.app.entity.Cliente;
import com.wisp.app.servicios.ClienteService;
import com.wisp.app.repository.AveriaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/averias")
public class AveriaController {

    private final AveriaRepository averiaRepo;
    private final ClienteService clienteService;

    public AveriaController(AveriaRepository averiaRepo, ClienteService clienteService) {
        this.averiaRepo = averiaRepo;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("averias", averiaRepo.findAll());
        return "averias";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("averia", new AveriaDto());
        model.addAttribute("clientes", clienteService.listarClientes());
        return "averias-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute AveriaDto dto) {

        Averia a;

        // NUEVA AVERÍA
        if (dto.getId() == null) {

            a = toEntity(dto);

            a.setFechaRegistro(java.time.LocalDate.now());

            if (a.getEstado() == null) {
                a.setEstado("Sin atender");
            }

        }

        // EDITAR AVERÍA
        else {

            a = averiaRepo.findById(dto.getId())
                    .orElseThrow();

            a.setCodigo(dto.getCodigo());
            a.setDescripcion(dto.getDescripcion());
            a.setDireccion(dto.getDireccion());
            a.setEstado(dto.getEstado());

            // Primera vez que pasa a Atendido
            if ("Atendido".equals(dto.getEstado())
                    && a.getFechaAtencion() == null) {

                a.setFechaAtencion(java.time.LocalDate.now());

            }

        }

        averiaRepo.save(a);

        return "redirect:/averias";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Averia a = averiaRepo.findById(id).orElseThrow();
        model.addAttribute("averia", toDto(a));
        model.addAttribute("clientes", clienteService.listarClientes());
        return "averias-form";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {
        Averia averia = averiaRepo.findById(id).orElseThrow();
        model.addAttribute("averia", averia);
        return "averias-info";
    }

    private Averia toEntity(AveriaDto dto) {

        if (dto == null)
            return null;

        Averia a = new Averia();

        a.setCodigo(dto.getCodigo());
        a.setDescripcion(dto.getDescripcion());
        a.setDireccion(dto.getDireccion());
        a.setEstado(dto.getEstado());

        if (dto.getClienteId() != null) {

            Cliente c = clienteService.obtenerClientePorId(dto.getClienteId());

            a.setCliente(c);

        }

        return a;
    }

private AveriaDto toDto(Averia a) {

    AveriaDto dto = new AveriaDto();

    dto.setId(a.getId());
    dto.setCodigo(a.getCodigo());
    dto.setDescripcion(a.getDescripcion());
    dto.setDireccion(a.getDireccion());
    dto.setEstado(a.getEstado());

    dto.setFechaAtencion(a.getFechaAtencion());
    dto.setFechaRegistro(a.getFechaRegistro());


    if (a.getCliente() != null) {

        dto.setClienteId(a.getCliente().getId());

        dto.setNombreCliente(
                a.getCliente().getNombres()
                + " "
                + a.getCliente().getApellidos()
        );
    }


    return dto;
}
}
