package com.wisp.app.servicios.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wisp.app.repository.ClienteRepository;
import com.wisp.app.repository.RedWanRepository;

@ExtendWith(MockitoExtension.class)
class RedWanServiceImplTest {

    @Mock
    private RedWanRepository redWanRepository;

    @Mock
    private ClienteRepository clienteRepository;

    private RedWanServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new RedWanServiceImpl(redWanRepository, clienteRepository);
    }

    @Test
    void eliminaRedSinClientes() {
        when(clienteRepository.existsByRedWanId(1L)).thenReturn(false);

        service.eliminar(1L);

        verify(redWanRepository).deleteById(1L);
    }

    @Test
    void noEliminaRedConClientesAsignados() {
        when(clienteRepository.existsByRedWanId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.eliminar(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("clientes");

        verify(redWanRepository, never()).deleteById(anyLong());
    }
}
