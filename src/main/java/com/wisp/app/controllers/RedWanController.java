package com.wisp.app.controllers;

import com.wisp.app.dto.RedWanDto;
import com.wisp.app.entity.RedWan;
import com.wisp.app.servicios.RedWanService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/redes-wan")
public class RedWanController {

    private final RedWanService redWanService;

    public RedWanController(RedWanService redWanService) {
        this.redWanService = redWanService;
    }

    @GetMapping
    public String listar(Model model) {

        model.addAttribute("redes", redWanService.listarTodas());

        return "redes-wan";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {

        model.addAttribute("red", new RedWanDto());

        return "redes-wan-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute RedWanDto dto) {

        RedWan red = toEntity(dto);

        redWanService.guardar(red);

        return "redirect:/redes-wan";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        RedWan red = redWanService.buscarPorId(id);

        model.addAttribute("red", toDto(red));

        return "redes-wan-form";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {

        model.addAttribute(
                "red",
                redWanService.buscarPorId(id));

        return "redes-wan-info";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {

        redWanService.eliminar(id);

        return "redirect:/redes-wan";
    }

    
    // Conversión DTO a Entity


    private RedWan toEntity(RedWanDto dto) {

        if (dto == null) {
            return null;
        }

        RedWan red = new RedWan();
        red.setId(dto.getId());
        red.setNombre(dto.getNombre());
        red.setDireccionRed(dto.getDireccionRed());
        red.setMascara(dto.getMascara());
        red.setGateway(dto.getGateway());
        red.setDns(dto.getDns());
        red.setEstado(dto.getEstado());

        return red;
    }

    
    // Conversión Entity a DTO
    

    private RedWanDto toDto(RedWan red) {

        if (red == null) {
            return new RedWanDto();
        }

        RedWanDto dto = new RedWanDto();

        dto.setId(red.getId());
        dto.setNombre(red.getNombre());
        dto.setDireccionRed(red.getDireccionRed());
        dto.setMascara(red.getMascara());
        dto.setGateway(red.getGateway());
        dto.setDns(red.getDns());
        dto.setEstado(red.getEstado());

        return dto;
    }
}