package com.wisp.app.controllers;

import com.wisp.app.entity.Planes;
import com.wisp.app.servicios.PlanesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/planes")
public class PlanesController {

    private final PlanesService planesService;

    public PlanesController(PlanesService planesService) {
        this.planesService = planesService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("planes", planesService.listarTodos());
        return "planes";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("plan", new Planes());
        return "planes-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Planes plan) {
        planesService.guardar(plan);
        return "redirect:/planes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("plan", planesService.buscarPorId(id));
        return "planes-form";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        planesService.eliminar(id);
        return "redirect:/planes";
    }
}