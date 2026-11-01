package com.uniquindio.CompraVenta.infrastructure.persistence.jpa;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import com.uniquindio.CompraVenta.domain.repository.RepuestoMasVendido;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Estas pruebas usan los datos de prueba que carga data.sql al arrancar. */
@SpringBootTest
@Transactional
class ConsultasConDatosDePruebaTest {

    @Autowired
    private CompraRepository compraRepository;

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
}
