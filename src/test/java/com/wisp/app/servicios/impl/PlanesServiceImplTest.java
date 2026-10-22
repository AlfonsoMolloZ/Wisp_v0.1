package com.wisp.app.servicios.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wisp.app.repository.ClienteRepository;
import com.wisp.app.repository.PlanesRepository;

@ExtendWith(MockitoExtension.class)
class PlanesServiceImplTest {

    @Mock
    private PlanesRepository planesRepository;

    @Mock
    private ClienteRepository clienteRepository;

    private PlanesServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PlanesServiceImpl(planesRepository, clienteRepository);
    }

    @Test
    void eliminaPlanSinClientes() {
        when(clienteRepository.existsByPlanId(1L)).thenReturn(false);

        service.eliminar(1L);

        verify(planesRepository).deleteById(1L);
    }

    @Test
    void noEliminaPlanConClientesAsignados() {
        when(clienteRepository.existsByPlanId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.eliminar(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("clientes");

        verify(planesRepository, never()).deleteById(anyLong());
    }

    @Test
    void buscarPorIdDevuelveNullSiNoExiste() {
        when(planesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(service.buscarPorId(99L)).isNull();
    }
}
