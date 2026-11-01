package com.uniquindio.CompraVenta.infrastructure.rest;

import com.uniquindio.CompraVenta.application.usecase.BuscarRepuestosCompatiblesUseCase;
import com.uniquindio.CompraVenta.application.usecase.EliminarRepuestoUseCase;
import com.uniquindio.CompraVenta.application.usecase.ListarRepuestosUseCase;
import com.uniquindio.CompraVenta.application.usecase.PublicarRepuestoUseCase;
import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.repository.Pagina;
import com.uniquindio.CompraVenta.domain.valueobject.Cilindraje;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import com.uniquindio.CompraVenta.infrastructure.rest.mapper.RepuestoRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RepuestoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(RepuestoRestMapper.class)
class RepuestoControllerTest {

    private static final String CUERPO_VALIDO = """
            {
              "nombre": "Pastillas de freno",
              "monto": 250000,
              "modelosCompatibles": [
                { "marca": "Yamaha", "version": "MT-07", "anio": 2022, "cilindrajeCc": 689 }
              ],
              "mesesGarantia": 12,
              "condicionesFabricante": "Garantia de fabrica",
              "esCategoriaAltoRiesgo": false
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PublicarRepuestoUseCase publicarRepuesto;

    @MockitoBean
    private ListarRepuestosUseCase listarRepuestos;

    @MockitoBean
    private BuscarRepuestosCompatiblesUseCase buscarCompatibles;

    @MockitoBean
    private EliminarRepuestoUseCase eliminarRepuesto;

    private Repuesto repuestoDePrueba() {
        Modelo modelo = new Modelo("Yamaha", "MT-07", 2022, new Cilindraje(689));
        return Repuesto.publicar("rep-1", "vend-1", "Pastillas de freno",
                Precio.enPesos(new BigDecimal("250000")), new Compatibilidad(List.of(modelo)),
                new Garantia(12, "Garantia de fabrica"));
    }

    @Test
    void deberiaPublicarYResponder201ConLocationUsandoElVendedorDelToken() throws Exception {
        // Arrange
        when(publicarRepuesto.ejecutar(any(), any(), any(), any(), any(), any(), anyBoolean()))
                .thenReturn(repuestoDePrueba());

        // Act & Assert
        mockMvc.perform(post("/api/repuestos")
                        .principal(() -> "vend-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUERPO_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nombre").value("Pastillas de freno"));

        verify(publicarRepuesto).ejecutar(any(), eq("vend-1"), eq("Pastillas de freno"),
                any(), any(), any(), eq(false));
    }

    @Test
    void noDeberiaPublicarSiFaltaElNombreNiLosModelos() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/repuestos")
                        .principal(() -> "vend-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "monto": 250000, "mesesGarantia": 12 }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").exists());

        verify(publicarRepuesto, never())
                .ejecutar(any(), any(), any(), any(), any(), any(), anyBoolean());
    }

    @Test
    void deberiaListarPaginadoYDevolverElContenidoYLosTotales() throws Exception {
        // Arrange
        when(listarRepuestos.ejecutar(any(), any(), anyInt(), anyInt()))
                .thenReturn(new Pagina<>(List.of(repuestoDePrueba()), 1, 1, 0, 10));

        // Act & Assert
        mockMvc.perform(get("/api/repuestos")
                        .param("texto", "pastillas")
                        .param("precioMaximo", "300000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElementos").value(1))
                .andExpect(jsonPath("$.totalPaginas").value(1))
                .andExpect(jsonPath("$.contenido[0].nombre").value("Pastillas de freno"));
    }

    @Test
    void deberiaResponder400SiLaPaginaEsInvalida() throws Exception {
        // Arrange
        when(listarRepuestos.ejecutar(any(), any(), anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException("La pagina no puede ser negativa."));

        // Act & Assert
        mockMvc.perform(get("/api/repuestos").param("pagina", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("La pagina no puede ser negativa."));
    }

    @Test
    void deberiaListarLosRepuestosCompatiblesConUnModelo() throws Exception {
        // Arrange
        when(buscarCompatibles.ejecutar(any(Modelo.class))).thenReturn(List.of(repuestoDePrueba()));

        // Act & Assert
        mockMvc.perform(get("/api/repuestos/compatibles")
                        .param("marca", "Yamaha")
                        .param("version", "MT-07")
                        .param("anio", "2022")
                        .param("cilindrajeCc", "689"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].repuestoId").value("rep-1"));
    }

    @Test
    void deberiaResponder400SiFaltaUnParametroDeLaBusquedaCompatible() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/repuestos/compatibles").param("marca", "Yamaha"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void deberiaEliminarYResponder204ConElVendedorDelToken() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/repuestos/rep-1").principal(() -> "vend-1"))
                .andExpect(status().isNoContent());

        verify(eliminarRepuesto).ejecutar("rep-1", "vend-1");
    }

    @Test
    void deberiaResponder422CuandoOtroVendedorIntentaEliminar() throws Exception {
        // Arrange
        when(eliminarRepuesto.ejecutar("rep-1", "otro-vendedor"))
                .thenThrow(new ReglaDominioException("Solo el vendedor que publico el repuesto puede eliminarlo."));

        // Act & Assert
        mockMvc.perform(delete("/api/repuestos/rep-1").principal(() -> "otro-vendedor"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensaje")
                        .value("Solo el vendedor que publico el repuesto puede eliminarlo."));
    }
}
