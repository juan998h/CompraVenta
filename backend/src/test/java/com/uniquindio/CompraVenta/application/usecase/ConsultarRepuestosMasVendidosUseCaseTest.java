package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import com.uniquindio.CompraVenta.domain.repository.RepuestoMasVendido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConsultarRepuestosMasVendidosUseCaseTest {

    private CompraRepository repository;
    private ConsultarRepuestosMasVendidosUseCase useCase;

    @BeforeEach
    void preparar() {
        repository = mock(CompraRepository.class);
        useCase = new ConsultarRepuestosMasVendidosUseCase(repository);
    }

    @Test
    void deberiaDevolverElRankingQueEntregaElRepositorio() {
        // Arrange
        when(repository.repuestosMasVendidos(2)).thenReturn(List.of(
                new RepuestoMasVendido("r-1", "Pastillas", 4L),
                new RepuestoMasVendido("r-2", "Escape", 3L)));

        // Act
        List<RepuestoMasVendido> ranking = useCase.ejecutar(2);

        // Assert
        assertEquals(2, ranking.size());
        assertEquals("r-1", ranking.get(0).repuestoId());
    }

    @Test
    void noDeberiaConsultarConUnLimiteFueraDeRango() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> useCase.ejecutar(0));
        assertThrows(IllegalArgumentException.class, () -> useCase.ejecutar(21));
        verify(repository, never()).repuestosMasVendidos(0);
    }
}
