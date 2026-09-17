package com.wisp.app.servicios.impl;

import com.wisp.app.NegocioException;
import com.wisp.app.entity.Usuario;
import com.wisp.app.repository.UsuarioRepository;
import com.wisp.app.servicios.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario guardarUsuario(Usuario usuario) {
        String username = usuario.getUsername();
        if (username == null || username.isBlank()) {
            throw new NegocioException("El nombre de usuario es obligatorio.");
        }

        if (usuario.getRole() == null
                || !(usuario.getRole().equals("ADMIN")
                        || usuario.getRole().equals("Tecnico")
                        || usuario.getRole().equals("USER"))) {
            throw new NegocioException("El rol seleccionado no es válido.");
        }

        if (usuario.getId() == null) {
            if (usuario.getPassword() == null || usuario.getPassword().length() < 4) {
                throw new NegocioException("La contraseña debe tener al menos 4 caracteres.");
            }
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        } else if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        usuarioRepository.findByUsername(username)
                .filter(existente -> usuario.getId() == null || !existente.getId().equals(usuario.getId()))
                .ifPresent(existente -> {
                    throw new NegocioException("El nombre de usuario ya existe.");
                });

        return usuarioRepository.save(usuario);
    }

    @Override
    public java.util.List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
}
