package com.uniquindio.CompraVenta.domain.valueobject;

import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CilindrajeTest {

    @Test
    void dosCilindrajesConElMismoValorDebenSerIguales() {
        // Arrange
        Cilindraje c1 = new Cilindraje(600);
        Cilindraje c2 = new Cilindraje(600);

        // Act & Assert
        assertEquals(c1, c2); // Value Object: igual por VALOR, no por instancia
    }

    @Test
    void noPermiteCrearCilindrajeMenorOIgualACero() {
        // Arrange & Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            new Cilindraje(0);
        });
    }
}