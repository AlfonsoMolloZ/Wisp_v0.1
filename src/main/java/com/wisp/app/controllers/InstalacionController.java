package com.wisp.app.controllers;

import com.wisp.app.dto.InstalacionDto;
import com.wisp.app.entity.Instalacion;
import com.wisp.app.entity.Cliente;
import com.wisp.app.repository.InstalacionRepository;
import com.wisp.app.servicios.ClienteService;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/instalaciones")
public class InstalacionController {

    private final InstalacionRepository instalacionRepo;
    private final ClienteService clienteService;

    public InstalacionController(InstalacionRepository instalacionRepo, ClienteService clienteService) {
        this.instalacionRepo = instalacionRepo;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model, @RequestParam( required = false) String estado) {
        if (estado != null) {
            model.addAttribute("instalaciones", instalacionRepo.findByEstado(estado));
        } else {
            model.addAttribute("instalaciones", instalacionRepo.findAll());
        }
        model.addAttribute("estadoSeleccionado", estado);

        return "instalaciones";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {

        model.addAttribute("instalacion", new InstalacionDto());

        model.addAttribute(
                "clientes",
                clienteService.buscarPorEstado("Nuevo"));

        return "instalaciones-form";

    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute InstalacionDto dto) {

        Instalacion i;

        // Nueva instalación
        if (dto.getId() == null) {

            i = toEntity(dto);

            // Siempre inicia como pendiente
            i.setEstado("Pendiente");

            // Fecha de creación de la orden
            i.setFechaRegistro(LocalDateTime.now());

        } else {

            // Editar instalación existente
            i = instalacionRepo.findById(dto.getId())
                    .orElseThrow();

            // Validación: solo se puede editar si está pendiente
            if (!"Pendiente".equals(i.getEstado())) {

                return "redirect:/instalaciones";
            }

            // Estados permitidos al editar
            if (!"Pendiente".equals(dto.getEstado())
                    && !"Atendido".equals(dto.getEstado())
                    && !"Anulado".equals(dto.getEstado())) {

                return "redirect:/instalaciones";
            }

            i.setEstado(dto.getEstado());
            i.setObservacion(dto.getObservacion());
            i.setCobrarInstalacion(dto.getCobrarInstalacion());
            i.setMontoInstalacion(dto.getMontoInstalacion());
            i.setMetodoPago(dto.getMetodoPago());

            // Registrar fecha de atención solo la primera vez
            if ("Atendido".equals(dto.getEstado())
                    && i.getFechaAtencion() == null) {

                i.setFechaAtencion(LocalDateTime.now());

            }

        }

        instalacionRepo.save(i);

        // Si la instalación fue atendida, activar el cliente cambianndo el estado
        if ("Atendido".equals(i.getEstado())
                && i.getPreCliente() != null) {

            Cliente cliente = i.getPreCliente();

            if (!"Activo".equals(cliente.getEstado())) {

                cliente.setEstado("Activo");
                clienteService.guardarCliente(cliente);

            }
        }

        return "redirect:/instalaciones";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Instalacion i = instalacionRepo.findById(id).orElseThrow();
        model.addAttribute("instalacion", toDto(i));
        model.addAttribute("clientes", clienteService.listarClientes());

        // validacion para que solo se pueda editar instalaciones con estado pendiente
        if (!"Pendiente".equals(i.getEstado())) {

            return "redirect:/instalaciones";

        }
        return "instalaciones-form";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {

        Instalacion instalacion = instalacionRepo.findById(id)
                .orElseThrow();

        model.addAttribute("instalacion", instalacion);

        return "instalaciones-info";
    }

    private Instalacion toEntity(InstalacionDto dto) {
        if (dto == null)
            return null;

        Instalacion i = new Instalacion();

        i.setId(dto.getId());
        i.setTipo(dto.getTipo());
        i.setEstado(dto.getEstado());
        i.setCobrarInstalacion(dto.getCobrarInstalacion());
        i.setMontoInstalacion(dto.getMontoInstalacion());
        i.setMetodoPago(dto.getMetodoPago());
        i.setObservacion(dto.getObservacion());

        i.setFechaRegistro(dto.getFechaRegistro());
        i.setFechaAtencion(dto.getFechaAtencion());

        if (dto.getClienteId() != null) {

            Cliente c = clienteService.obtenerClientePorId(dto.getClienteId());

            i.setPreCliente(c);

            // La dirección siempre se toma del cliente
            i.setDireccion(c.getDireccion());
        }

        return i;
    }

    private InstalacionDto toDto(Instalacion i) {

        InstalacionDto dto = new InstalacionDto();
        // validacion para evitar error de nullpointerexception al obtener el nombre del
        // cliente
        if (i.getPreCliente() != null) {
            dto.setClienteId(i.getPreCliente().getId());
            dto.setNombreCliente(i.getPreCliente().getNombre());
        }
        dto.setId(i.getId());
        dto.setTipo(i.getTipo());
        dto.setDireccion(i.getDireccion());
        dto.setEstado(i.getEstado());
        dto.setCobrarInstalacion(i.getCobrarInstalacion());
        dto.setMontoInstalacion(i.getMontoInstalacion());
        dto.setMetodoPago(i.getMetodoPago());
        dto.setObservacion(i.getObservacion());

        dto.setFechaRegistro(i.getFechaRegistro());
        dto.setFechaAtencion(i.getFechaAtencion());

        return dto;
    }
}
