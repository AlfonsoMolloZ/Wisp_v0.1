package com.wisp.app.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
class GlobalControllerAdviceTest {

    private final GlobalControllerAdvice advice = new GlobalControllerAdvice();

    @Mock
    private Authentication authentication;

    @Test
    void sinAutenticacionDevuelveVacio() {
        assertThat(advice.obtenerRol(null)).isEmpty();
    }

    @Test
    void extraeRolDeLasAutoridades() {
        doReturn(List.<GrantedAuthority>of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(authentication).getAuthorities();

        assertThat(advice.obtenerRol(authentication)).isEqualTo("ADMIN");
    }

    @Test
    void devuelveVacioSiNoHayRol() {
        doReturn(List.<GrantedAuthority>of(new SimpleGrantedAuthority("PERM_X")))
                .when(authentication).getAuthorities();

        assertThat(advice.obtenerRol(authentication)).isEmpty();
    }
}
