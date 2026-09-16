package com.wisp.app.controllers;

import com.wisp.app.dto.UsuarioDto;
import com.wisp.app.entity.Usuario;
import com.wisp.app.servicios.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        return "usuarios";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new UsuarioDto());
        model.addAttribute("roles", new String[] { "ADMIN", "Tecnico" });
        return "usuarios-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute UsuarioDto dto, Model model) {
        try {
            Usuario u = toEntity(dto);
            usuarioService.guardarUsuario(u);
            return "redirect:/usuarios";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", dto);
            model.addAttribute("roles", new String[] { "ADMIN", "Tecnico" });
            return "usuarios-form";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Usuario u = usuarioService.obtenerUsuarioPorId(id);
        model.addAttribute("usuario", toDto(u));
        model.addAttribute("roles", new String[] { "ADMIN", "Tecnico" });
        return "usuarios-form";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:/usuarios";
    }

    private Usuario toEntity(UsuarioDto dto) {
        if (dto == null)
            return null;
        Usuario u = new Usuario();
        if (dto.getId() != null)
            u.setId(dto.getId().intValue());
        u.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isBlank())
            u.setPassword(dto.getPassword());
        u.setRole(dto.getRole());
        return u;
    }

    private UsuarioDto toDto(Usuario u) {
        UsuarioDto dto = new UsuarioDto();
        if (u == null)
            return dto;
        dto.setId(u.getId() != null ? u.getId().longValue() : null);
        dto.setUsername(u.getUsername());
        dto.setRole(u.getRole());
        // do not set password
        return dto;
    }
}
