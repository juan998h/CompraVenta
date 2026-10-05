package com.uniquindio.CompraVenta.domain.entity;

import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.valueobject.Cilindraje;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Especialidad;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VendedorEspecializadoTest {

    private VendedorEspecializado vendedor() {
        return VendedorEspecializado.registrar("vendedor-1", "Taller Sur", Especialidad.ECU);
    }

    private Repuesto publicarComo(VendedorEspecializado vendedor, boolean altoRiesgo) {
        Modelo modelo = new Modelo("Kawasaki", "Z900", 2023, new Cilindraje(948));
        return vendedor.publicarRepuesto("repuesto-1", "Mapa ECU",
                Precio.enPesos(new BigDecimal("150000")),
                new Compatibilidad(List.of(modelo)), new Garantia(6, "Garantia de fabrica"), altoRiesgo);
    }

    @Test
    void dosVendedoresConElMismoIdSonElMismoAunqueSusDatosDifieran() {
        // Arrange
        VendedorEspecializado original = vendedor();
        VendedorEspecializado otro = VendedorEspecializado.registrar("vendedor-1", "Otro taller",
                Especialidad.SUSPENSION);

        // Act & Assert
        assertEquals(original, otro); // Entidad: igual por IDENTIDAD
    }

    @Test
    void noPermiteRegistrarUnVendedorSinNombre() {
        // Arrange & Act & Assert
        assertThrows(ReglaDominioException.class, () ->
                VendedorEspecializado.registrar("vendedor-1", " ", Especialidad.ECU));
    }

    @Test
    void noDebePermitirPublicarEnAltoRiesgoAUnVendedorNoVerificado() {
        // Arrange
        VendedorEspecializado vendedor = vendedor();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> publicarComo(vendedor, true));
        assertFalse(vendedor.isVerificado()); // el estado no cambio tras el rechazo
    }

    @Test
    void permitePublicarEnAltoRiesgoAUnVendedorVerificado() {
        // Arrange
        VendedorEspecializado vendedor = vendedor();
        vendedor.verificarEspecialidad();

        // Act
        Repuesto repuesto = publicarComo(vendedor, true);

        // Assert
        assertEquals("vendedor-1", repuesto.getVendedorId());
    }

    @Test
    void noDebePermitirVerificarDosVecesAlMismoVendedor() {
        // Arrange
        VendedorEspecializado vendedor = vendedor();
        vendedor.verificarEspecialidad();

        // Act & Assert
        assertThrows(ReglaDominioException.class, vendedor::verificarEspecialidad);
        assertTrue(vendedor.isVerificado()); // el estado no cambio tras el rechazo
    }
}