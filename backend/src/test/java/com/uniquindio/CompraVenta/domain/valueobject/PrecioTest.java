package com.uniquindio.CompraVenta.domain.valueobject;

import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PrecioTest {

    @Test
    void dosPreciosConElMismoValorDebenSerIgualesAunqueSeEscribanDistinto() {
        // Arrange
        Precio p1 = Precio.enPesos(new BigDecimal("15000"));
        Precio p2 = new Precio(new BigDecimal("15000.00"), "cop");

        // Act & Assert
        assertEquals(p1, p2); // Value Object: igual por VALOR
    }

    @Test
    void noPermitePrecioMenorOIgualACero() {
        // Arrange & Act & Assert
        assertThrows(ReglaDominioException.class, () -> Precio.enPesos(BigDecimal.ZERO));
    }
}