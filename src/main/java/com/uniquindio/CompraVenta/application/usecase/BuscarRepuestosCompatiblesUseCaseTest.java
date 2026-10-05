package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;
import com.uniquindio.CompraVenta.domain.valueobject.Cilindraje;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import com.uniquindio.CompraVenta.infrastructure.persistence.RepuestoRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuscarRepuestosCompatiblesUseCaseTest {

    private static final Modelo Z900 = new Modelo("Kawasaki", "Z900", 2023, new Cilindraje(948));
    private static final Modelo NINJA = new Modelo("Kawasaki", "Ninja ZX-6R", 2023, new Cilindraje(636));

    private RepuestoRepository repository;
    private BuscarRepuestosCompatiblesUseCase useCase;

    @BeforeEach
    void preparar() {
        repository = new RepuestoRepositoryEnMemoria();
        useCase = new BuscarRepuestosCompatiblesUseCase(repository);
    }

    private Repuesto repuestoPara(String id, Modelo modelo) {
        return Repuesto.publicar(id, "vendedor-1", "Kit de arrastre",
                Precio.enPesos(new BigDecimal("150000")),
                new Compatibilidad(List.of(modelo)), new Garantia(6, "Garantia de fabrica"));
    }

    @Test
    void devuelveSoloLosRepuestosCompatiblesConElModeloBuscado() {
        // Arrange
        repository.guardar(repuestoPara("r-z900", Z900));
        repository.guardar(repuestoPara("r-ninja", NINJA));

        // Act
        List<Repuesto> resultado = useCase.ejecutar(Z900);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("r-z900", resultado.get(0).getId());
    }

    @Test
    void noDevuelveRepuestosEliminados() {
        // Arrange
        Repuesto eliminado = repuestoPara("r-z900", Z900);
        eliminado.eliminarLogicamente();
        repository.guardar(eliminado);

        // Act
        List<Repuesto> resultado = useCase.ejecutar(Z900);

        // Assert
        assertTrue(resultado.isEmpty());
    }
}