package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.valueobject.EstadoCompra;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import com.uniquindio.CompraVenta.infrastructure.persistence.CompraRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListarComprasPendientesUseCaseTest {

    private Compra compra(String id, LocalDateTime fecha) {
        return Compra.realizar(id, "rep-1", "ana", Precio.enPesos(new BigDecimal("15000")), fecha);
    }

    @Test
    void deberiaListarSoloLasComprasPendientesDeLaMasAntiguaALaMasReciente() {
        // Arrange
        CompraRepositoryEnMemoria repository = new CompraRepositoryEnMemoria();
        Compra reciente = compra("c-2", LocalDateTime.of(2026, 10, 2, 10, 0));
        Compra antigua = compra("c-1", LocalDateTime.of(2026, 10, 1, 10, 0));
        Compra yaConfirmada = compra("c-3", LocalDateTime.of(2026, 10, 3, 10, 0));
        yaConfirmada.completar();
        repository.guardar(reciente);
        repository.guardar(antigua);
        repository.guardar(yaConfirmada);
        ListarComprasPendientesUseCase useCase = new ListarComprasPendientesUseCase(repository);

        // Act
        List<Compra> pendientes = useCase.ejecutar();

        // Assert
        assertEquals(2, pendientes.size());
        assertEquals("c-1", pendientes.get(0).getId());
        assertEquals("c-2", pendientes.get(1).getId());
        assertTrue(pendientes.stream().allMatch(c -> c.getEstado() == EstadoCompra.PENDIENTE));
    }

    @Test
    void deberiaDevolverListaVaciaSiNoHayComprasPendientes() {
        // Arrange
        ListarComprasPendientesUseCase useCase =
                new ListarComprasPendientesUseCase(new CompraRepositoryEnMemoria());

        // Act & Assert
        assertTrue(useCase.ejecutar().isEmpty());
    }
}