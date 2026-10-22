package com.wisp.app.servicios.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.wisp.app.entity.Usuario;
import com.wisp.app.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private UsuarioServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UsuarioServiceImpl(usuarioRepository, passwordEncoder);
    }

    @Test
    void creaUsuarioConPasswordEncriptada() {
        Usuario usuario = usuario("ana", "secreta", "ADMIN");
        when(usuarioRepository.existsByUsername("ana")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario guardado = service.guardarUsuario(usuario);

        assertThat(guardado.getPassword()).isNotEqualTo("secreta");
        assertThat(passwordEncoder.matches("secreta", guardado.getPassword())).isTrue();
    }

    @Test
    void rechazaCreacionSinPassword() {
        Usuario usuario = usuario("ana", " ", "ADMIN");

        assertThatThrownBy(() -> service.guardarUsuario(usuario))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("contraseña");
    }

    @Test
    void conservaPasswordAlEditarSinNuevaPassword() {
        Usuario existente = usuario("ana", "hash-viejo", "Tecnico");
        existente.setId(7);
        Usuario cambios = usuario("ana", "", "ADMIN");
        cambios.setId(7);

        when(usuarioRepository.existsByUsername("ana")).thenReturn(true);
        when(usuarioRepository.findByUsername("ana")).thenReturn(Optional.of(existente));
        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(existente)).thenReturn(existente);

        Usuario guardado = service.guardarUsuario(cambios);

        assertThat(guardado.getPassword()).isEqualTo("hash-viejo");
        assertThat(guardado.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void rechazaUsernameDeOtroUsuario() {
        Usuario existente = usuario("ana", "hash", "Tecnico");
        existente.setId(7);
        Usuario cambios = usuario("ana", "nueva", "ADMIN");
        cambios.setId(8);

        when(usuarioRepository.existsByUsername("ana")).thenReturn(true);
        when(usuarioRepository.findByUsername("ana")).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> service.guardarUsuario(cambios))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ya está registrado");
    }

    private Usuario usuario(String username, String password, String role) {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(password);
        usuario.setRole(role);
        return usuario;
    }
}
