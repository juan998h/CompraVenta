package com.uniquindio.CompraVenta.domain.service;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import com.uniquindio.CompraVenta.domain.valueobject.Cilindraje;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
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

class ServicioDeEliminacionDeRepuestoTest {

    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 10, 4, 10, 0);

    private CompraRepository compraRepository;
    private ServicioDeEliminacionDeRepuesto servicio;

    @BeforeEach
    void preparar() {
        compraRepository = new CompraRepositoryEnMemoria();
        servicio = new ServicioDeEliminacionDeRepuesto(compraRepository);
    }

    private Repuesto repuestoValido() {
        Modelo modelo = new Modelo("Kawasaki", "Z900", 2023, new Cilindraje(948));
        return Repuesto.publicar("repuesto-1", "vendedor-1", "Kit de arrastre",
                Precio.enPesos(new BigDecimal("150000")),
                new Compatibilidad(List.of(modelo)), new Garantia(6, "Garantia de fabrica"));
    }

    @Test
    void noDebePermitirEliminarUnRepuestoConComprasActivas() {
        // Arrange
        Repuesto repuesto = repuestoValido();
        compraRepository.guardar(Compra.realizar("compra-1", repuesto.getId(), "comprador-1",
                repuesto.getPrecio(), AHORA));

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> servicio.eliminar(repuesto));
        assertFalse(repuesto.isEliminadoLogicamente()); // el estado no cambio tras el rechazo
    }

    @Test
    void permiteEliminarUnRepuestoSinComprasActivas() {
        // Arrange
        Repuesto repuesto = repuestoValido();

        // Act
        servicio.eliminar(repuesto);

        // Assert
        assertTrue(repuesto.isEliminadoLogicamente());
    }
}