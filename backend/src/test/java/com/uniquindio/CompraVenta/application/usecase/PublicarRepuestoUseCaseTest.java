package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.entity.VendedorEspecializado;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;
import com.uniquindio.CompraVenta.domain.repository.VendedorRepository;
import com.uniquindio.CompraVenta.domain.valueobject.Cilindraje;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Especialidad;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import com.uniquindio.CompraVenta.infrastructure.persistence.RepuestoRepositoryEnMemoria;
import com.uniquindio.CompraVenta.infrastructure.persistence.VendedorRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PublicarRepuestoUseCaseTest {

    private RepuestoRepository repuestoRepository;
    private VendedorRepository vendedorRepository;
    private PublicarRepuestoUseCase useCase;

    @BeforeEach
    void preparar() {
        repuestoRepository = new RepuestoRepositoryEnMemoria();
        vendedorRepository = new VendedorRepositoryEnMemoria();
        useCase = new PublicarRepuestoUseCase(repuestoRepository, vendedorRepository);
        vendedorRepository.guardar(VendedorEspecializado.registrar("vendedor-1", "Taller Sur", Especialidad.ECU));
    }

    private Compatibilidad compatibilidadValida() {
        Modelo modelo = new Modelo("Kawasaki", "Z900", 2023, new Cilindraje(948));
        return new Compatibilidad(List.of(modelo));
    }

    private Precio precioValido() {
        return Precio.enPesos(new BigDecimal("150000"));
    }

    private Garantia garantiaValida() {
        return new Garantia(6, "Garantia de fabrica");
    }

    @Test
    void publicarUnRepuestoLoGuardaEnElRepositorioConSuVendedor() {
        // Arrange & Act
        Repuesto publicado = useCase.ejecutar("repuesto-1", "vendedor-1", "Kit de arrastre",
                precioValido(), compatibilidadValida(), garantiaValida(), false);

        // Assert
        Repuesto guardado = repuestoRepository.obtenerPorId("repuesto-1").orElseThrow();
        assertEquals(publicado, guardado);
        assertEquals("vendedor-1", guardado.getVendedorId());
    }

    @Test
    void noPermitePublicarSiElVendedorNoExiste() {
        // Arrange & Act & Assert
        assertThrows(ReglaDominioException.class, () ->
                useCase.ejecutar("repuesto-1", "no-existe", "Kit de arrastre",
                        precioValido(), compatibilidadValida(), garantiaValida(), false));
        assertTrue(repuestoRepository.obtenerPorId("repuesto-1").isEmpty()); // no se guardo nada
    }

    @Test
    void noPermitePublicarEnAltoRiesgoSiElVendedorNoEstaVerificado() {
        // Arrange & Act & Assert
        assertThrows(ReglaDominioException.class, () ->
                useCase.ejecutar("repuesto-1", "vendedor-1", "Mapa ECU",
                        precioValido(), compatibilidadValida(), garantiaValida(), true));
        assertTrue(repuestoRepository.obtenerPorId("repuesto-1").isEmpty()); // no se guardo nada
    }
}