package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;
import com.uniquindio.CompraVenta.domain.service.ServicioDeCompra;
import com.uniquindio.CompraVenta.domain.valueobject.Cilindraje;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.EstadoCompra;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import com.uniquindio.CompraVenta.infrastructure.persistence.CompraRepositoryEnMemoria;
import com.uniquindio.CompraVenta.infrastructure.persistence.RepuestoRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RealizarCompraUseCaseTest {

    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 10, 4, 10, 0);

    private RepuestoRepository repuestoRepository;
    private CompraRepository compraRepository;
    private RealizarCompraUseCase useCase;

    @BeforeEach
    void preparar() {
        repuestoRepository = new RepuestoRepositoryEnMemoria();
        compraRepository = new CompraRepositoryEnMemoria();
        useCase = new RealizarCompraUseCase(repuestoRepository, compraRepository,
                new ServicioDeCompra(compraRepository));
    }

    @Test
    void realizarUnaCompraLaGuardaEnElRepositorioComoPendiente() {
        // Arrange
        Modelo modelo = new Modelo("Kawasaki", "Z900", 2023, new Cilindraje(948));
        repuestoRepository.guardar(Repuesto.publicar("repuesto-1", "vendedor-1", "Kit de arrastre",
                Precio.enPesos(new BigDecimal("150000")),
                new Compatibilidad(List.of(modelo)), new Garantia(6, "Garantia de fabrica")));

        // Act
        Compra compra = useCase.ejecutar("compra-1", "comprador-1", "repuesto-1", AHORA);

        // Assert
        Compra guardada = compraRepository.obtenerPorId("compra-1").orElseThrow();
        assertEquals(compra, guardada);
        assertEquals(EstadoCompra.PENDIENTE, guardada.getEstado());
    }

    @Test
    void noPermiteComprarUnRepuestoQueNoExiste() {
        // Arrange & Act & Assert
        assertThrows(ReglaDominioException.class, () ->
                useCase.ejecutar("compra-1", "comprador-1", "no-existe", AHORA));
        assertTrue(compraRepository.obtenerPorId("compra-1").isEmpty()); // no se guardo nada
    }
}