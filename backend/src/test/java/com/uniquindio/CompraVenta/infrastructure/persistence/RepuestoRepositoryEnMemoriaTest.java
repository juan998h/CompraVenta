package com.uniquindio.CompraVenta.infrastructure.persistence;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.repository.Pagina;
import com.uniquindio.CompraVenta.domain.valueobject.Cilindraje;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RepuestoRepositoryEnMemoriaTest {

    private RepuestoRepositoryEnMemoria repository;

    @BeforeEach
    void preparar() {
        repository = new RepuestoRepositoryEnMemoria();
        repository.guardar(repuesto("r-1", "Pastillas de freno", "250000"));
        repository.guardar(repuesto("r-2", "Pastillas ceramicas", "400000"));
        repository.guardar(repuesto("r-3", "Filtro de aceite", "80000"));
        Repuesto eliminado = repuesto("r-4", "Pastillas viejas", "50000");
        eliminado.eliminarLogicamente();
        repository.guardar(eliminado);
    }

    private Repuesto repuesto(String id, String nombre, String precio) {
        Modelo modelo = new Modelo("Yamaha", "MT-07", 2022, new Cilindraje(689));
        return Repuesto.publicar(id, "vendedor-1", nombre, Precio.enPesos(new BigDecimal(precio)),
                new Compatibilidad(List.of(modelo)), new Garantia(6, "Garantia de fabrica"));
    }

    @Test
    void deberiaFiltrarPorTextoYNoMostrarLosEliminados() {
        // Act
        Pagina<Repuesto> pagina = repository.buscarDisponibles("pastillas", null, 0, 10);

        // Assert
        assertEquals(2, pagina.totalElementos());
        assertEquals("Pastillas ceramicas", pagina.contenido().get(0).getNombre());
    }

    @Test
    void deberiaFiltrarPorPrecioMaximoYPaginar() {
        // Act
        Pagina<Repuesto> primera = repository.buscarDisponibles("", new BigDecimal("300000"), 0, 1);
        Pagina<Repuesto> segunda = repository.buscarDisponibles("", new BigDecimal("300000"), 1, 1);

        // Assert
        assertEquals(2, primera.totalElementos());
        assertEquals(2, primera.totalPaginas());
        assertEquals(1, primera.contenido().size());
        assertEquals(1, segunda.contenido().size());
    }
}
