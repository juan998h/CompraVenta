package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;
import com.uniquindio.CompraVenta.domain.service.ServicioDeCompra;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Cilindraje;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RealizarCompraUseCaseFallosTest {

    private RepuestoRepository repuestoRepository;
    private CompraRepository compraRepository;
    private RealizarCompraUseCase useCase;

    @BeforeEach
    void preparar() {
        repuestoRepository = mock(RepuestoRepository.class);
        compraRepository = mock(CompraRepository.class);
        useCase = new RealizarCompraUseCase(repuestoRepository, compraRepository,
                new ServicioDeCompra(compraRepository));
    }

    private Repuesto repuestoDePrueba() {
        Compatibilidad compatibilidad = new Compatibilidad(
                List.of(new Modelo("Yamaha", "MT-07", 2022, new Cilindraje(689))));
        return Repuesto.publicar("rep-1", "vendedor-1", "Pastillas de freno",
                Precio.enPesos(new BigDecimal("150000")), compatibilidad,
                new Garantia(6, "Garantia de fabrica"));
    }

    @Test
    void noDeberiaGuardarSiElRepuestoNoExiste() {
        // Arrange
        when(repuestoRepository.obtenerPorId("no-existe")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> useCase.ejecutar("c-1", "ana", "no-existe", LocalDateTime.now()));
        verify(compraRepository, never()).guardar(any(Compra.class));
    }

    @Test
    void noDeberiaGuardarSiElRepuestoFueEliminado() {
        // Arrange
        Repuesto eliminado = repuestoDePrueba();
        eliminado.eliminarLogicamente();
        when(repuestoRepository.obtenerPorId("rep-1")).thenReturn(Optional.of(eliminado));

        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> useCase.ejecutar("c-1", "ana", "rep-1", LocalDateTime.now()));
        verify(compraRepository, never()).guardar(any(Compra.class));
    }

    @Test
    void noDeberiaGuardarSiElCompradorYaTieneUnaCompraActiva() {
        // Arrange
        when(repuestoRepository.obtenerPorId("rep-1")).thenReturn(Optional.of(repuestoDePrueba()));
        when(compraRepository.existeCompraActiva("ana", "rep-1")).thenReturn(true);

        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> useCase.ejecutar("c-1", "ana", "rep-1", LocalDateTime.now()));
        verify(compraRepository, never()).guardar(any(Compra.class));
    }
}