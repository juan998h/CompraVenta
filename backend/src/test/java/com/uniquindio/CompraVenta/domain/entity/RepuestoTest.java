package com.uniquindio.CompraVenta.domain.entity;

import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.valueobject.Cilindraje;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepuestoTest {

    private Compatibilidad compatibilidadValida() {
        Modelo modelo = new Modelo("Kawasaki", "Z900", 2023, new Cilindraje(948));
        return new Compatibilidad(List.of(modelo));
    }

    private Garantia garantiaValida() {
        return new Garantia(6, "Garantia de fabrica");
    }

    private Repuesto repuestoValido(String id) {
        return Repuesto.publicar(id, "vendedor-1", "Kit de arrastre", new BigDecimal("150000"),
                compatibilidadValida(), garantiaValida());
    }

    @Test
    void dosRepuestosConElMismoIdSonElMismoAunqueSusDatosDifieran() {
        // Arrange
        Repuesto original = repuestoValido("1");
        Repuesto otro = Repuesto.publicar("1", "vendedor-1", "Pastillas de freno",
                new BigDecimal("90000"), compatibilidadValida(), garantiaValida());

        // Act & Assert
        assertEquals(original, otro); // Entidad: igual por IDENTIDAD
    }

    @Test
    void dosRepuestosConIdDistintoNoSonElMismoAunqueTenganLosMismosDatos() {
        // Arrange
        Repuesto r1 = repuestoValido("1");
        Repuesto r2 = repuestoValido("2");

        // Act & Assert
        assertNotEquals(r1, r2);
    }

    @Test
    void noPermiteCrearRepuestoSinModeloCompatible() {
        // Arrange
        Compatibilidad sinModelos = new Compatibilidad(List.of());

        // Act & Assert
        assertThrows(ReglaDominioException.class, () ->
                Repuesto.publicar("1", "vendedor-1", "Kit de arrastre", new BigDecimal("150000"),
                        sinModelos, garantiaValida()));
    }

    @Test
    void noPermitePrecioMenorOIgualACero() {
        // Arrange & Act & Assert
        assertThrows(ReglaDominioException.class, () ->
                Repuesto.publicar("1", "vendedor-1", "Kit de arrastre", BigDecimal.ZERO,
                        compatibilidadValida(), garantiaValida()));
    }

    @Test
    void noPermiteCrearRepuestoSinGarantia() {
        // Arrange & Act & Assert
        assertThrows(ReglaDominioException.class, () ->
                Repuesto.publicar("1", "vendedor-1", "Kit de arrastre", new BigDecimal("150000"),
                        compatibilidadValida(), null));
    }

    @Test
    void noDebePermitirEliminarUnRepuestoYaEliminado() {
        // Arrange
        Repuesto repuesto = repuestoValido("1");
        repuesto.eliminarLogicamente();

        // Act & Assert
        assertThrows(ReglaDominioException.class, repuesto::eliminarLogicamente);
        assertTrue(repuesto.isEliminadoLogicamente()); // el estado no cambio tras el rechazo
    }

    @Test
    void noDebePermitirVenderUnRepuestoEliminado() {
        // Arrange
        Repuesto repuesto = repuestoValido("1");
        repuesto.eliminarLogicamente();

        // Act & Assert
        assertThrows(ReglaDominioException.class, repuesto::venderUnidad);
        assertTrue(repuesto.isEliminadoLogicamente()); // el estado no cambio tras el rechazo
    }
}