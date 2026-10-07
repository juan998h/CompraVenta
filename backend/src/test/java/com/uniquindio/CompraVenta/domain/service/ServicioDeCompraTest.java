package com.uniquindio.CompraVenta.domain.service;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import com.uniquindio.CompraVenta.domain.valueobject.Cilindraje;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.EstadoCompra;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import com.uniquindio.CompraVenta.infrastructure.persistence.CompraRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServicioDeCompraTest {

    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 10, 4, 10, 0);

    private CompraRepository compraRepository;
    private ServicioDeCompra servicio;

    @BeforeEach
    void preparar() {
        compraRepository = new CompraRepositoryEnMemoria();
        servicio = new ServicioDeCompra(compraRepository);
    }

    private Repuesto repuestoValido() {
        Modelo modelo = new Modelo("Kawasaki", "Z900", 2023, new Cilindraje(948));
        return Repuesto.publicar("repuesto-1", "vendedor-1", "Kit de arrastre",
                Precio.enPesos(new BigDecimal("150000")),
                new Compatibilidad(List.of(modelo)), new Garantia(6, "Garantia de fabrica"));
    }

    @Test
    void noDebePermitirComprarUnRepuestoConUnaCompraActivaDelMismoComprador() {
        // Arrange
        Repuesto repuesto = repuestoValido();
        Compra primera = servicio.realizarCompra("compra-1", "comprador-1", repuesto, AHORA);
        compraRepository.guardar(primera);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () ->
                servicio.realizarCompra("compra-2", "comprador-1", repuesto, AHORA));
        assertEquals(EstadoCompra.PENDIENTE, primera.getEstado()); // la primera compra sigue igual
    }

    @Test
    void noDebePermitirComprarUnRepuestoEliminado() {
        // Arrange
        Repuesto repuesto = repuestoValido();
        repuesto.eliminarLogicamente();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () ->
                servicio.realizarCompra("compra-1", "comprador-1", repuesto, AHORA));
        assertTrue(repuesto.isEliminadoLogicamente()); // el estado no cambio tras el rechazo
    }

    @Test
    void permiteComprarDeNuevoUnRepuestoCuyaCompraAnteriorFueReembolsada() {
        // Arrange
        Repuesto repuesto = repuestoValido();
        Compra primera = servicio.realizarCompra("compra-1", "comprador-1", repuesto, AHORA);
        primera.completar();
        primera.solicitarReembolso("No sirvio", AHORA.plusDays(1));
        compraRepository.guardar(primera);

        // Act
        Compra segunda = servicio.realizarCompra("compra-2", "comprador-1", repuesto, AHORA.plusDays(2));

        // Assert
        assertEquals(EstadoCompra.PENDIENTE, segunda.getEstado());
    }
}