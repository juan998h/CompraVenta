package com.uniquindio.CompraVenta.infrastructure.persistence.jpa;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import com.uniquindio.CompraVenta.domain.repository.ReembolsosPorComprador;
import com.uniquindio.CompraVenta.domain.valueobject.EstadoCompra;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class CompraRepositoryJpaTest {

    @Autowired
    private CompraRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deberiaGuardarYRecuperarUnaCompraConSuPrecioYEstado() {
        // Arrange
        Compra compra = Compra.realizar("T-001", "repuesto-test", "comprador-test",
                Precio.enPesos(new BigDecimal("15000")), LocalDateTime.of(2026, 10, 1, 10, 0));

        // Act
        repository.guardar(compra);
        entityManager.flush();
        entityManager.clear();
        Compra recuperada = repository.obtenerPorId("T-001").orElseThrow();

        // Assert
        assertEquals("T-001", recuperada.getId());
        assertEquals("repuesto-test", recuperada.getRepuestoId());
        assertEquals(new BigDecimal("15000.00"), recuperada.getPrecioPagado().monto());
        assertEquals("COP", recuperada.getPrecioPagado().moneda());
        assertEquals(EstadoCompra.PENDIENTE, recuperada.getEstado());
    }

    @Test
    void deberiaRankearCompradoresPorReembolsosDeMayorAMenor() {
        // Act (los datos salen de data.sql: diego tiene 3 reembolsos y carla 1)
        List<ReembolsosPorComprador> ranking = repository.compradoresConMasReembolsos(2);

        // Assert
        assertEquals(2, ranking.size());
        assertEquals("diego", ranking.get(0).compradorId());
        assertEquals(3L, ranking.get(0).cantidad());
        assertEquals("carla", ranking.get(1).compradorId());
        assertEquals(1L, ranking.get(1).cantidad());
    }
}