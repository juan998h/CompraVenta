package com.uniquindio.CompraVenta.infrastructure.persistence.jpa;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.repository.Pagina;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;
import com.uniquindio.CompraVenta.domain.valueobject.Cilindraje;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import com.uniquindio.CompraVenta.infrastructure.persistence.jpa.mapper.RepuestoPersistenceMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({RepuestoRepositoryJpa.class, RepuestoPersistenceMapper.class})
class RepuestoRepositoryDataJpaTest {

    private static final Modelo MT07 = new Modelo("Yamaha", "MT-07", 2022, new Cilindraje(689));
    private static final Modelo Z900 = new Modelo("Kawasaki", "Z900", 2023, new Cilindraje(948));

    @Autowired
    private RepuestoRepository repository;

    @Autowired
    private EntityManager entityManager;

    private Repuesto nuevoRepuesto(String id, String nombre, String precio, Modelo... modelos) {
        return Repuesto.publicar(id, "vendedor-x", nombre, Precio.enPesos(new BigDecimal(precio)),
                new Compatibilidad(List.of(modelos)), new Garantia(6, "Garantia de fabrica"));
    }

    private void guardarYLimpiar(Repuesto repuesto) {
        repository.guardar(repuesto);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void deberiaGuardarYRecuperarUnRepuestoConSusModelosPrecioYGarantia() {
        // Arrange
        guardarYLimpiar(nuevoRepuesto("DJ-001", "Repuesto de prueba uno", "150000", MT07, Z900));

        Repuesto recuperado = repository.obtenerPorId("DJ-001").orElseThrow();

        assertEquals("Repuesto de prueba uno", recuperado.getNombre());
        assertEquals("vendedor-x", recuperado.getVendedorId());
        assertEquals(new BigDecimal("150000.00"), recuperado.getPrecio().monto());
        assertEquals(6, recuperado.obtenerGarantia().mesesCobertura());
        assertEquals(2, recuperado.getCompatibilidad().modelosCompatibles().size());
        assertTrue(recuperado.esCompatibleCon(Z900));
    }

    @Test
    void laEliminacionLogicaMarcaLaFilaPeroNoLaBorra() {
        // Arrange
        Repuesto repuesto = nuevoRepuesto("DJ-002", "Repuesto para eliminar", "90000", MT07);
        guardarYLimpiar(repuesto);

        // Act
        repuesto.eliminarLogicamente();
        guardarYLimpiar(repuesto);

        // Assert: la fila sigue en la tabla, solo cambia la marca
        Number filas = (Number) entityManager
                .createNativeQuery("SELECT COUNT(*) FROM repuestos WHERE id = 'DJ-002'")
                .getSingleResult();
        assertEquals(1L, filas.longValue());
        assertTrue(repository.obtenerPorId("DJ-002").orElseThrow().isEliminadoLogicamente());
    }

    @Test
    void elListadoNoDeberiaMostrarLosRepuestosEliminados() {
        // Arrange
        Repuesto visible = nuevoRepuesto("DJ-003", "Zzz listado visible", "50000", MT07);
        Repuesto oculto = nuevoRepuesto("DJ-004", "Zzz listado oculto", "50000", MT07);
        oculto.eliminarLogicamente();
        repository.guardar(visible);
        repository.guardar(oculto);
        entityManager.flush();
        entityManager.clear();

        Pagina<Repuesto> pagina = repository.buscarDisponibles("zzz listado", null, 0, 10);

        assertEquals(1, pagina.totalElementos());
        assertEquals("DJ-003", pagina.contenido().get(0).getId());
    }

    @Test
    void deberiaFiltrarPorTextoYPrecioMaximoYPaginarElResultado() {
        // Arrange: tres repuestos con un texto propio, a precios distintos
        repository.guardar(nuevoRepuesto("DJ-005", "Qqq pagina uno", "10000", MT07));
        repository.guardar(nuevoRepuesto("DJ-006", "Qqq pagina dos", "20000", MT07));
        repository.guardar(nuevoRepuesto("DJ-007", "Qqq pagina tres", "90000", MT07));
        entityManager.flush();
        entityManager.clear();

        // Act
        Pagina<Repuesto> baratos = repository.buscarDisponibles("qqq pagina", new BigDecimal("30000"), 0, 10);
        Pagina<Repuesto> primera = repository.buscarDisponibles("qqq pagina", null, 0, 2);
        Pagina<Repuesto> segunda = repository.buscarDisponibles("qqq pagina", null, 1, 2);

        // Assert
        assertEquals(2, baratos.totalElementos());
        assertEquals(3, primera.totalElementos());
        assertEquals(2, primera.totalPaginas());
        assertEquals(2, primera.contenido().size());
        assertEquals(1, segunda.contenido().size());
    }

    @Test
    void deberiaEncontrarLosRepuestosCompatiblesConUnModeloYNoLosEliminados() {
        // Arrange
        Modelo modeloRaro = new Modelo("Ducati", "Panigale V4 prueba", 2024, new Cilindraje(1103));
        Repuesto compatible = nuevoRepuesto("DJ-008", "Compatible con panigale", "70000", modeloRaro);
        Repuesto eliminado = nuevoRepuesto("DJ-009", "Eliminado con panigale", "70000", modeloRaro);
        eliminado.eliminarLogicamente();
        repository.guardar(compatible);
        repository.guardar(eliminado);
        entityManager.flush();
        entityManager.clear();


        List<Repuesto> encontrados = repository.obtenerDisponiblesCompatiblesCon(modeloRaro);

        assertEquals(1, encontrados.size());
        assertEquals("DJ-008", encontrados.get(0).getId());
        assertFalse(encontrados.get(0).isEliminadoLogicamente());
    }
}
