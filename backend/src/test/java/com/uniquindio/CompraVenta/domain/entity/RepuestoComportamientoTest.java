package com.uniquindio.CompraVenta.domain.entity;

import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.valueobject.Cilindraje;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepuestoComportamientoTest {

    private static final Modelo MT07 = new Modelo("Yamaha", "MT-07", 2022, new Cilindraje(689));
    private static final Modelo CBR500 = new Modelo("Honda", "CBR500R", 2021, new Cilindraje(471));
    private static final Modelo PCX = new Modelo("Honda", "PCX", 2022, new Cilindraje(150));

    private Repuesto repuestoCon(Modelo... modelos) {
        return Repuesto.publicar("rep-1", "vendedor-1", "Pastillas de freno",
                Precio.enPesos(new BigDecimal("150000")),
                new Compatibilidad(List.of(modelos)),
                new Garantia(6, "Garantia de fabrica"));
    }

    @Test
    void deberiaRenombrarElRepuesto() {
        // Arrange
        Repuesto repuesto = repuestoCon(MT07);

        // Act
        repuesto.renombrar("Pastillas ceramicas");

        // Assert
        assertEquals("Pastillas ceramicas", repuesto.getNombre());
    }

    @Test
    void noDebePermitirRenombrarConUnNombreVacio() {
        // Arrange
        Repuesto repuesto = repuestoCon(MT07);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> repuesto.renombrar("  "));
        assertEquals("Pastillas de freno", repuesto.getNombre());
    }

    @Test
    void deberiaCambiarElPrecio() {
        // Arrange
        Repuesto repuesto = repuestoCon(MT07);
        Precio nuevoPrecio = Precio.enPesos(new BigDecimal("180000"));

        // Act
        repuesto.cambiarPrecio(nuevoPrecio);

        // Assert
        assertEquals(nuevoPrecio, repuesto.getPrecio());
    }

    @Test
    void noDebePermitirCambiarElPrecioDeUnRepuestoEliminado() {
        // Arrange
        Repuesto repuesto = repuestoCon(MT07);
        Precio precioOriginal = repuesto.getPrecio();
        repuesto.eliminarLogicamente();

        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> repuesto.cambiarPrecio(Precio.enPesos(new BigDecimal("180000"))));
        assertEquals(precioOriginal, repuesto.getPrecio());
    }

    @Test
    void deberiaAgregarUnModeloCompatible() {
        // Arrange
        Repuesto repuesto = repuestoCon(MT07);

        // Act
        repuesto.agregarModeloCompatible(CBR500);

        // Assert
        assertTrue(repuesto.esCompatibleCon(MT07));
        assertTrue(repuesto.esCompatibleCon(CBR500));
    }

    @Test
    void noDebePermitirAgregarUnModeloQueYaEsCompatible() {
        // Arrange
        Repuesto repuesto = repuestoCon(MT07);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> repuesto.agregarModeloCompatible(MT07));
    }

    @Test
    void deberiaQuitarUnModeloCuandoQuedaAlMenosOtro() {
        // Arrange
        Repuesto repuesto = repuestoCon(MT07, CBR500);

        // Act
        repuesto.quitarModeloCompatible(CBR500);

        // Assert
        assertFalse(repuesto.esCompatibleCon(CBR500));
        assertTrue(repuesto.esCompatibleCon(MT07));
    }

    @Test
    void noDebePermitirQuitarElUnicoModeloCompatible() {
        // Arrange
        Repuesto repuesto = repuestoCon(MT07);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> repuesto.quitarModeloCompatible(MT07));
        assertTrue(repuesto.esCompatibleCon(MT07));
    }

    @Test
    void deberiaReactivarUnRepuestoEliminado() {
        // Arrange
        Repuesto repuesto = repuestoCon(MT07);
        repuesto.eliminarLogicamente();

        // Act
        repuesto.reactivar();

        // Assert
        assertTrue(repuesto.estaDisponible());
    }

    @Test
    void noDebePermitirReactivarUnRepuestoQueNoEstaEliminado() {
        // Arrange
        Repuesto repuesto = repuestoCon(MT07);

        // Act & Assert
        assertThrows(ReglaDominioException.class, repuesto::reactivar);
    }

    @Test
    void deberiaDetectarSiTodosSusModelosSonDeAltoCilindraje() {
        // Act & Assert
        assertTrue(repuestoCon(MT07, CBR500).esDeAltoCilindraje());
        assertFalse(repuestoCon(MT07, PCX).esDeAltoCilindraje());
    }

    @Test
    void noDebePermitirEliminarAUnVendedorQueNoEsElDueno() {
        // Arrange
        Repuesto repuesto = repuestoCon(MT07);

        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> repuesto.verificarQueLoPuedeEliminar("otro-vendedor"));
    }

    @Test
    void deberiaPermitirEliminarAlVendedorDueno() {
        // Arrange
        Repuesto repuesto = repuestoCon(MT07);

        // Act & Assert
        assertDoesNotThrow(() -> repuesto.verificarQueLoPuedeEliminar("vendedor-1"));
    }
}