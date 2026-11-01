package com.uniquindio.CompraVenta.infrastructure.persistence.jpa;

import com.uniquindio.CompraVenta.domain.entity.VendedorEspecializado;
import com.uniquindio.CompraVenta.domain.repository.VendedorRepository;
import com.uniquindio.CompraVenta.domain.valueobject.Especialidad;
import com.uniquindio.CompraVenta.infrastructure.persistence.jpa.mapper.VendedorPersistenceMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({VendedorRepositoryJpa.class, VendedorPersistenceMapper.class})
class VendedorRepositoryDataJpaTest {

    @Autowired
    private VendedorRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deberiaGuardarYRecuperarUnVendedorNoVerificado() {
        // Arrange
        repository.guardar(VendedorEspecializado.registrar("VJ-001", "Taller de prueba", Especialidad.ECU));
        entityManager.flush();
        entityManager.clear();

        // Act
        VendedorEspecializado recuperado = repository.obtenerPorId("VJ-001").orElseThrow();

        // Assert
        assertEquals("Taller de prueba", recuperado.getNombre());
        assertEquals(Especialidad.ECU, recuperado.getEspecialidad());
        assertFalse(recuperado.isVerificado());
    }

    @Test
    void deberiaConservarLaVerificacionDespuesDeGuardar() {
        // Arrange
        VendedorEspecializado vendedor = VendedorEspecializado.registrar("VJ-002", "Garage verificado", Especialidad.SUSPENSION);
        repository.guardar(vendedor);
        entityManager.flush();
        entityManager.clear();

        // Act
        VendedorEspecializado cargado = repository.obtenerPorId("VJ-002").orElseThrow();
        cargado.verificarEspecialidad();
        repository.guardar(cargado);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertTrue(repository.obtenerPorId("VJ-002").orElseThrow().isVerificado());
    }
}
