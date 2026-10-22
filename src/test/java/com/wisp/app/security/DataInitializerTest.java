package com.wisp.app.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.wisp.app.entity.Usuario;
import com.wisp.app.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private DataInitializer initializer;

    @BeforeEach
    void setUp() {
        initializer = new DataInitializer(usuarioRepository, passwordEncoder, "ADMIN", "secreta");
    }

    @Test
    void noCreaAdminSiNoHayPassword() {
        when(usuarioRepository.count()).thenReturn(0L);
        DataInitializer sinPassword = new DataInitializer(usuarioRepository, passwordEncoder, "ADMIN", " ");

        sinPassword.run();

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void creaAdminConPasswordEncriptada() {
        when(usuarioRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode("secreta")).thenReturn("hash");

        initializer.run();

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario admin = captor.getValue();
        assertThat(admin.getUsername()).isEqualTo("ADMIN");
        assertThat(admin.getPassword()).isEqualTo("hash");
        assertThat(admin.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void noCreaAdminSiYaHayUsuarios() {
        when(usuarioRepository.count()).thenReturn(1L);

        initializer.run();

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verifyNoInteractions(passwordEncoder);
    }
}
