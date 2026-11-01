package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.repository.Pagina;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ListarRepuestosUseCaseTest {

    private RepuestoRepository repository;
    private ListarRepuestosUseCase useCase;

    @BeforeEach
    void preparar() {
        repository = new RepuestoRepositoryEnMemoria();
        useCase = new ListarRepuestosUseCase(repository);
        Modelo modelo = new Modelo("Yamaha", "MT-07", 2022, new Cilindraje(689));
        repository.guardar(Repuesto.publicar("r-1", "vend-1", "Pastillas de freno",
                Precio.enPesos(new BigDecimal("250000")), new Compatibilidad(List.of(modelo)),
                new Garantia(6, "Garantia de fabrica")));
        repository.guardar(Repuesto.publicar("r-2", "vend-1", "Filtro de aceite",
                Precio.enPesos(new BigDecimal("80000")), new Compatibilidad(List.of(modelo)),
                new Garantia(6, "Garantia de fabrica")));
    }

    @Test
    void deberiaListarConTextoYPrecioMaximo() {
        // Act
        Pagina<Repuesto> pagina = useCase.ejecutar("filtro", new BigDecimal("100000"), 0, 10);

        // Assert
        assertEquals(1, pagina.totalElementos());
        assertEquals("r-2", pagina.contenido().get(0).getId());
    }

    @Test
    void noDeberiaAceptarUnaPaginaNegativa() {
        assertThrows(IllegalArgumentException.class, () -> useCase.ejecutar("", null, -1, 10));
    }

    @Test
    void noDeberiaAceptarUnTamanoFueraDeRango() {
        assertThrows(IllegalArgumentException.class, () -> useCase.ejecutar("", null, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> useCase.ejecutar("", null, 0, 51));
    }

    @Test
    void noDeberiaAceptarUnPrecioMaximoNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> useCase.ejecutar("", new BigDecimal("-1"), 0, 10));
    }
}
