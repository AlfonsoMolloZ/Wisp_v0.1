package com.wisp.app.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.wisp.app.entity.Usuario;
import com.wisp.app.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class DatabaseUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private DatabaseUserDetailsService service;

    @BeforeEach
    void setUp() {
        service = new DatabaseUserDetailsService(usuarioRepository);
    }

    @Test
    void cargaUsuarioConSuRol() {
        Usuario usuario = usuario("ana", "hash", "ADMIN");
        when(usuarioRepository.findByUsername("ana")).thenReturn(Optional.of(usuario));

        UserDetails detalles = service.loadUserByUsername("ana");

        assertThat(detalles.getUsername()).isEqualTo("ana");
        assertThat(detalles.getPassword()).isEqualTo("hash");
        assertThat(detalles.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void lanzaExcepcionSiElUsuarioNoExiste() {
        when(usuarioRepository.findByUsername("fantasma")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("fantasma"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("fantasma");
    }

    @Test
    void usaRolUserSiElRolEsNulo() {
        Usuario usuario = usuario("ana", "hash", null);
        when(usuarioRepository.findByUsername("ana")).thenReturn(Optional.of(usuario));

        UserDetails detalles = service.loadUserByUsername("ana");

        assertThat(detalles.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }

    private Usuario usuario(String username, String password, String role) {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(password);
        usuario.setRole(role);
        return usuario;
    }
}
