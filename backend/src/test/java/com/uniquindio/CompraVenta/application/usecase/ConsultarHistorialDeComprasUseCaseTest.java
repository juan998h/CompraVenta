package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import com.uniquindio.CompraVenta.infrastructure.persistence.CompraRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConsultarHistorialDeComprasUseCaseTest {

    private CompraRepository repository;
    private ConsultarHistorialDeComprasUseCase useCase;

    @BeforeEach
    void preparar() {
        repository = new CompraRepositoryEnMemoria();
        useCase = new ConsultarHistorialDeComprasUseCase(repository);
    }

    private Compra compra(String id, String comprador, int dia) {
        return Compra.realizar(id, "rep-" + id, comprador,
                Precio.enPesos(new BigDecimal("100000")), LocalDateTime.of(2026, 10, dia, 10, 0));
    }

    @Test
    void deberiaListarSoloLasComprasDelCompradorDeLaMasRecienteALaMasAntigua() {
        // Arrange
        repository.guardar(compra("c-1", "ana", 1));
        repository.guardar(compra("c-2", "ana", 5));
        repository.guardar(compra("c-3", "bruno", 3));

        // Act
        List<Compra> historial = useCase.ejecutar("ana");

        // Assert
        assertEquals(2, historial.size());
        assertEquals("c-2", historial.get(0).getId());
        assertEquals("c-1", historial.get(1).getId());
    }

    @Test
    void noDeberiaConsultarSinIndicarElComprador() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> useCase.ejecutar(" "));
    }
}
