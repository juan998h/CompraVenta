package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConfirmarPagoUseCaseFallosTest {

    @Test
    void noDeberiaGuardarSiLaCompraNoExiste() {
        // Arrange
        CompraRepository repository = mock(CompraRepository.class);
        when(repository.obtenerPorId("no-existe")).thenReturn(Optional.empty());
        ConfirmarPagoUseCase useCase = new ConfirmarPagoUseCase(repository);

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> useCase.ejecutar("no-existe"));
        verify(repository, never()).guardar(any(Compra.class));
    }
}