package com.uniquindio.CompraVenta.infrastructure.persistence.jpa;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import com.uniquindio.CompraVenta.domain.repository.Pagina;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;
import com.uniquindio.CompraVenta.domain.repository.RepuestoMasVendido;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Estas pruebas usan los datos de prueba que carga data.sql al arrancar. */
@SpringBootTest
@Transactional
class ConsultasConDatosDePruebaTest {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private RepuestoRepository repuestoRepository;

    @Test
    void elRankingDeRepuestosMasVendidosUneComprasConRepuestosYCuentaLasCompletadas() {
        // Act (en data.sql: pastillas 4 completadas, escape 3, filtro 2, kit 1, espejos 0)
        List<RepuestoMasVendido> ranking = compraRepository.repuestosMasVendidos(3);

        // Assert
        assertEquals(3, ranking.size());
        assertEquals("pastillas-freno", ranking.get(0).repuestoId());
        assertEquals("Pastillas de freno deportivas", ranking.get(0).nombre());
        assertEquals(4L, ranking.get(0).unidades());
        assertEquals("escape-deportivo", ranking.get(1).repuestoId());
        assertEquals(3L, ranking.get(1).unidades());
        assertEquals("filtro-aceite", ranking.get(2).repuestoId());
        assertEquals(2L, ranking.get(2).unidades());
    }

    @Test
    void elHistorialDeUnCompradorSaleDeLaMasRecienteALaMasAntigua() {
        // Act (en data.sql ana tiene C-001, C-002 y C-014)
        List<Compra> historial = compraRepository.obtenerPorComprador("ana");

        // Assert
        assertEquals(3, historial.size());
        assertEquals("C-014", historial.get(0).getId());
        assertEquals("C-002", historial.get(1).getId());
        assertEquals("C-001", historial.get(2).getId());
    }

    @Test
    void elListadoPaginadoNoMuestraElRepuestoEliminadoDeLosDatosDePrueba() {
        // Act (data.sql tiene 6 repuestos y uno, baul-antiguo, esta eliminado)
        Pagina<Repuesto> pagina = repuestoRepository.buscarDisponibles("", null, 0, 10);

        // Assert
        assertEquals(5, pagina.totalElementos());
        assertEquals(1, pagina.totalPaginas());
        assertTrue(pagina.contenido().stream().noneMatch(r -> r.getId().equals("baul-antiguo")));
    }

    @Test
    void elListadoPaginadoDivideElResultadoEnPaginas() {
        // Act
        Pagina<Repuesto> primera = repuestoRepository.buscarDisponibles("", null, 0, 2);
        Pagina<Repuesto> tercera = repuestoRepository.buscarDisponibles("", null, 2, 2);

        // Assert (5 repuestos de 2 en 2 son 3 paginas: 2 + 2 + 1)
        assertEquals(5, primera.totalElementos());
        assertEquals(3, primera.totalPaginas());
        assertEquals(2, primera.contenido().size());
        assertEquals(1, tercera.contenido().size());
    }

    @Test
    void elListadoPaginadoFiltraPorTextoYPorPrecioMaximo() {
        // Act
        Pagina<Repuesto> porTexto = repuestoRepository.buscarDisponibles("filtro", null, 0, 10);
        Pagina<Repuesto> porPrecio = repuestoRepository.buscarDisponibles("", new BigDecimal("100000"), 0, 10);

        // Assert (a 100000 o menos: filtro de aceite 80000 y kit de arrastre 50000)
        assertEquals(1, porTexto.totalElementos());
        assertEquals("filtro-aceite", porTexto.contenido().get(0).getId());
        assertEquals(2, porPrecio.totalElementos());
    }
}
