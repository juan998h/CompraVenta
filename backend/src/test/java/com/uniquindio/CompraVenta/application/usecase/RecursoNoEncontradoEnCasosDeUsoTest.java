package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.entity.VendedorEspecializado;
import com.uniquindio.CompraVenta.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;
import com.uniquindio.CompraVenta.domain.repository.VendedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Cuando lo que se busca no existe, el caso de uso lanza "no encontrado" (la API responde 404). */
class RecursoNoEncontradoEnCasosDeUsoTest {

    private CompraRepository compraRepository;
    private VendedorRepository vendedorRepository;

    @BeforeEach
    void preparar() {
        compraRepository = mock(CompraRepository.class);
        vendedorRepository = mock(VendedorRepository.class);
        when(compraRepository.obtenerPorId("no-existe")).thenReturn(Optional.empty());
        when(vendedorRepository.obtenerPorId("no-existe")).thenReturn(Optional.empty());
    }

    @Test
    void registrarDescargaDeUnaCompraQueNoExisteLanzaNoEncontrado() {
        // Arrange
        RegistrarDescargaUseCase useCase = new RegistrarDescargaUseCase(compraRepository);

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> useCase.ejecutar("no-existe", LocalDateTime.now()));
        verify(compraRepository, never()).guardar(any(Compra.class));
    }

    @Test
    void solicitarReembolsoDeUnaCompraQueNoExisteLanzaNoEncontrado() {
        // Arrange
        SolicitarReembolsoUseCase useCase = new SolicitarReembolsoUseCase(compraRepository);

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> useCase.ejecutar("no-existe", "No me sirvio", LocalDateTime.now()));
        verify(compraRepository, never()).guardar(any(Compra.class));
    }

    @Test
    void verificarUnVendedorQueNoExisteLanzaNoEncontrado() {
        // Arrange
        VerificarEspecialidadVendedorUseCase useCase =
                new VerificarEspecialidadVendedorUseCase(vendedorRepository);

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> useCase.ejecutar("no-existe"));
        verify(vendedorRepository, never()).guardar(any(VendedorEspecializado.class));
    }

    @Test
    void publicarConUnVendedorQueNoExisteLanzaNoEncontrado() {
        // Arrange
        RepuestoRepository repuestoRepository = mock(RepuestoRepository.class);
        PublicarRepuestoUseCase useCase =
                new PublicarRepuestoUseCase(repuestoRepository, vendedorRepository);

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> useCase.ejecutar("r-1", "no-existe", "Pastillas", null, null, null, false));
        verify(repuestoRepository, never()).guardar(any(Repuesto.class));
    }
}
