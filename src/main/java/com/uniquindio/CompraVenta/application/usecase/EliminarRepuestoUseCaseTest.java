package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;
import com.uniquindio.CompraVenta.domain.service.ServicioDeEliminacionDeRepuestos;
import com.uniquindio.CompraVenta.domain.valueobject.Cilindraje;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import com.uniquindio.CompraVenta.infrastructure.persistence.CompraRepositoryEnMemoria;
import com.uniquindio.CompraVenta.infrastructure.persistence.RepuestoRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EliminarRepuestoUseCaseTest {

    private RepuestoRepository repuestoRepository;
    private EliminarRepuestoUseCase useCase;

    @BeforeEach
    void preparar() {
        repuestoRepository = new RepuestoRepositoryEnMemoria();
        useCase = new EliminarRepuestoUseCase(repuestoRepository,
                new ServicioDeEliminacionDeRepuestos(new CompraRepositoryEnMemoria()));
    }

    @Test
    void eliminarUnRepuestoLoDejaMarcadoComoEliminadoEnElRepositorio() {
        // Arrange
        Modelo modelo = new Modelo("Kawasaki", "Z900", 2023, new Cilindraje(948));
        repuestoRepository.guardar(Repuesto.publicar("repuesto-1", "vendedor-1", "Kit de arrastre",
                Precio.enPesos(new BigDecimal("150000")),
                new Compatibilidad(List.of(modelo)), new Garantia(6, "Garantia de fabrica")));

        // Act
        useCase.ejecutar("repuesto-1");

        // Assert
        assertTrue(repuestoRepository.obtenerPorId("repuesto-1").orElseThrow().isEliminadoLogicamente());
    }

    @Test
    void noPermiteEliminarUnRepuestoQueNoExiste() {
        // Arrange & Act & Assert
        assertThrows(ReglaDominioException.class, () -> useCase.ejecutar("no-existe"));
    }
}