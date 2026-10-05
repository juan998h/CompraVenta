package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.VendedorEspecializado;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.repository.VendedorRepository;
import com.uniquindio.CompraVenta.domain.valueobject.Especialidad;
import com.uniquindio.CompraVenta.infrastructure.persistence.VendedorRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VerificarEspecialidadVendedorUseCaseTest {

    private VendedorRepository repository;
    private VerificarEspecialidadVendedorUseCase useCase;

    @BeforeEach
    void preparar() {
        repository = new VendedorRepositoryEnMemoria();
        useCase = new VerificarEspecialidadVendedorUseCase(repository);
        repository.guardar(VendedorEspecializado.registrar("vendedor-1", "Taller Sur", Especialidad.ECU));
    }

    @Test
    void verificarUnVendedorLoDejaVerificadoEnElRepositorio() {
        // Arrange & Act
        useCase.ejecutar("vendedor-1");

        // Assert
        assertTrue(repository.obtenerPorId("vendedor-1").orElseThrow().isVerificado());
    }

    @Test
    void noPermiteVerificarUnVendedorQueNoExiste() {
        // Arrange & Act & Assert
        assertThrows(ReglaDominioException.class, () -> useCase.ejecutar("no-existe"));
    }

    @Test
    void noPermiteVerificarDosVecesAlMismoVendedor() {
        // Arrange
        useCase.ejecutar("vendedor-1");

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> useCase.ejecutar("vendedor-1"));
        assertTrue(repository.obtenerPorId("vendedor-1").orElseThrow().isVerificado()); // el estado no cambio
    }
}