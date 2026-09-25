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

    @Test
    void dosRepuestosConDatosDistintosNoSonElMismoPorquesusIdsSonDiferentes() {
        // Arrange
        Repuesto r1 = new Repuesto("Kit de arrastre", new BigDecimal("150000"),
                compatibilidadValida(), new Garantia(6, "Garantia de fabrica"));
        Repuesto r2 = new Repuesto("Kit de arrastre", new BigDecimal("150000"),
                compatibilidadValida(), new Garantia(6, "Garantia de fabrica"));

        // Act & Assert
        assertNotEquals(r1, r2); //  cada instancia tiene su propio id generado, no son la misma
    }

    @Test
    void noPermiteCrearRepuestoSinModeloCompatible() {
        Compatibilidad sinModelos = new Compatibilidad(List.of());
        assertThrows(ReglaDominioException.class, () -> {
            new Repuesto("Kit de arrastre", new BigDecimal("150000"),
                    sinModelos, new Garantia(6, "Garantia de fabrica"));
        });
    }

    @Test
    void noDebePermitirEliminarUnRepuestoYaEliminado() {
        Repuesto repuesto = new Repuesto("Kit de arrastre", new BigDecimal("150000"),
                compatibilidadValida(), new Garantia(6, "Garantia de fabrica"));
        repuesto.eliminarLogicamente(); // primera eliminacion, valida

        assertThrows(ReglaDominioException.class, () -> {
            repuesto.eliminarLogicamente(); // segundo intento, debe fallar
        });
        assertTrue(repuesto.isEliminadoLogicamente()); // el estado sigue igual, no se rompio nada
    }

    @Test
    void noPermitePrecioMenorOIgualACero() {
        // Arrange & Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            new Repuesto("Kit de arrastre", new BigDecimal("0"),
                    compatibilidadValida(), new Garantia(6, "Garantia de fabrica"));
        });
    }
    @Test
    void noDebePermitirVenderUnRepuestoEliminado() {
        // Arrange
        Repuesto repuesto = new Repuesto("Kit de arrastre", new BigDecimal("150000"),
                compatibilidadValida(), new Garantia(6, "Garantia de fabrica"));
        repuesto.eliminarLogicamente();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            repuesto.venderUnidad();
        });
        assertTrue(repuesto.isEliminadoLogicamente()); // el estado sigue igual, no se rompio nada
    }
}