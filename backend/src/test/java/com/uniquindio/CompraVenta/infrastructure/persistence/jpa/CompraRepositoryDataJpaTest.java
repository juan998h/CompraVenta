package com.uniquindio.CompraVenta.infrastructure.persistence.jpa;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import com.uniquindio.CompraVenta.infrastructure.persistence.jpa.mapper.CompraPersistenceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({CompraRepositoryJpa.class, CompraPersistenceMapper.class})
class CompraRepositoryDataJpaTest {

    @Autowired
    private CompraRepository repository;

    private Compra nuevaCompra(String id) {
        return Compra.realizar(id, "repuesto-x", "comprador-x",
                Precio.enPesos(new BigDecimal("50000")), LocalDateTime.of(2026, 10, 1, 10, 0));
    }

    @Test
    void deberiaDetectarUnaCompraActivaDelMismoCompradorYRepuesto() {
        // Arrange
        repository.guardar(nuevaCompra("D-001"));

        // Act & Assert
        assertTrue(repository.existeCompraActiva("comprador-x", "repuesto-x"));
        assertTrue(repository.existeCompraActivaDelRepuesto("repuesto-x"));
    }

    @Test
    void noDeberiaConsiderarActivaUnaCompraReembolsada() {
        // Arrange
        Compra compra = nuevaCompra("D-002");
        compra.completar();
        compra.solicitarReembolso("No era compatible", LocalDateTime.of(2026, 10, 2, 10, 0));
        repository.guardar(compra);

        // Act & Assert
        assertFalse(repository.existeCompraActiva("comprador-x", "repuesto-x"));
    }
}