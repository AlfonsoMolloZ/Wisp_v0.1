package com.wisp.app.servicios.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wisp.app.entity.Usuario;
import com.wisp.app.repository.UsuarioRepository;
import com.wisp.app.servicios.UsuarioService;

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

        // Edición: recuperar el usuario existente
        if (usuario.getId() != null) {
            Usuario existente = usuarioRepository
                    .findById(usuario.getId().longValue())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Usuario no encontrado"));

            existente.setUsername(usuario.getUsername());
            existente.setRole(usuario.getRole());

            // Solo cambiar la contraseña si se ingresó una nueva
            if (usuario.getPassword() != null
                    && !usuario.getPassword().isBlank()) {
                existente.setPassword(
                        passwordEncoder.encode(usuario.getPassword()));
            }

            return usuarioRepository.save(existente);
        }

        // Creación: la contraseña es obligatoria
        if (usuario.getPassword() == null
                || usuario.getPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria al crear un usuario");
        }

        usuario.setPassword(
                passwordEncoder.encode(usuario.getPassword()));

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
