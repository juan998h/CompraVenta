package com.uniquindio.CompraVenta.domain.entity;

import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.valueobject.EstadoCompra;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CompraTest {

    private static final LocalDateTime FECHA = LocalDateTime.of(2026, 10, 1, 10, 0);

    private Precio precio(String monto) {
        return Precio.enPesos(new BigDecimal(monto));
    }

    private Compra compraPendiente(String id) {
        return Compra.realizar(id, "repuesto-1", "comprador-1", precio("150000"), FECHA);
    }

    private Compra compraCompletada() {
        Compra compra = compraPendiente("1");
        compra.completar();
        return compra;
    }

    @Test
    void dosComprasConElMismoIdSonLaMismaAunqueSusDatosDifieran() {
        // Arrange
        Compra original = compraPendiente("1");
        Compra otra = Compra.realizar("1", "repuesto-2", "comprador-2", precio("90000"), FECHA);

        // Act & Assert
        assertEquals(original, otra); // Entidad: igual por IDENTIDAD
    }

    @Test
    void noPermiteCrearCompraSinComprador() {
        // Arrange & Act & Assert
        assertThrows(ReglaDominioException.class, () ->
                Compra.realizar("1", "repuesto-1", " ", precio("150000"), FECHA));
    }

    @Test
    void noDebePermitirReembolsarUnaCompraPendiente() {
        // Arrange
        Compra compra = compraPendiente("1");

        // Act & Assert
        assertThrows(ReglaDominioException.class, () ->
                compra.solicitarReembolso("No sirvio", FECHA.plusDays(1)));
        assertEquals(EstadoCompra.PENDIENTE, compra.getEstado()); // no cambio nada
    }

    @Test
    void noDebePermitirReembolsarFueraDelPlazo() {
        // Arrange
        Compra compra = compraCompletada();
        LocalDateTime fueraDePlazo = FECHA.plusDays(Compra.PLAZO_REEMBOLSO_DIAS + 1);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () ->
                compra.solicitarReembolso("No sirvio", fueraDePlazo));
        assertEquals(EstadoCompra.COMPLETADA, compra.getEstado()); // no cambio nada
    }

    @Test
    void permiteReembolsarUnaCompraCompletadaDentroDelPlazo() {
        // Arrange
        Compra compra = compraCompletada();

        // Act
        compra.solicitarReembolso("No sirvio", FECHA.plusDays(2));

        // Assert
        assertEquals(EstadoCompra.REEMBOLSADA, compra.getEstado());
    }

    @Test
    void noDebePermitirReembolsarUnaCompraCuyoArchivoYaFueDescargado() {
        // Arrange
        Compra compra = compraCompletada();
        compra.registrarDescarga(FECHA.plusDays(1));

        // Act & Assert
        assertThrows(ReglaDominioException.class, () ->
                compra.solicitarReembolso("No sirvio", FECHA.plusDays(2)));
        assertEquals(EstadoCompra.COMPLETADA, compra.getEstado()); // no cambio el estado
        assertTrue(compra.fueDescargada());                        // la descarga sigue registrada
    }

    @Test
    void noDebePermitirDescargarUnaCompraPendiente() {
        // Arrange
        Compra compra = compraPendiente("1");

        // Act & Assert
        assertThrows(ReglaDominioException.class, () ->
                compra.registrarDescarga(FECHA.plusDays(1)));
        assertFalse(compra.fueDescargada()); // no se registro nada
    }

    @Test
    void noDebePermitirDescargarUnaCompraReembolsada() {
        // Arrange
        Compra compra = compraCompletada();
        compra.solicitarReembolso("No sirvio", FECHA.plusDays(1));

        // Act & Assert
        assertThrows(ReglaDominioException.class, () ->
                compra.registrarDescarga(FECHA.plusDays(2)));
        assertEquals(EstadoCompra.REEMBOLSADA, compra.getEstado()); // no cambio el estado
        assertFalse(compra.fueDescargada());                        // no se registro nada
    }
}