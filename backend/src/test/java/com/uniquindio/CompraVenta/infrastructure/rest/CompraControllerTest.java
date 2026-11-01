package com.uniquindio.CompraVenta.infrastructure.rest;

import com.uniquindio.CompraVenta.application.usecase.ConfirmarPagoUseCase;
import com.uniquindio.CompraVenta.application.usecase.ConsultarHistorialDeComprasUseCase;
import com.uniquindio.CompraVenta.application.usecase.RealizarCompraUseCase;
import com.uniquindio.CompraVenta.application.usecase.RegistrarDescargaUseCase;
import com.uniquindio.CompraVenta.application.usecase.SolicitarReembolsoUseCase;
import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import com.uniquindio.CompraVenta.infrastructure.rest.mapper.CompraRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompraController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CompraRestMapper.class)
class CompraControllerTest {

    private static final String CUERPO_VALIDO = """
            { "repuestoId": "rep-1" }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RealizarCompraUseCase realizarCompra;

    @MockitoBean
    private ConfirmarPagoUseCase confirmarPago;

    @MockitoBean
    private RegistrarDescargaUseCase registrarDescarga;

    @MockitoBean
    private SolicitarReembolsoUseCase solicitarReembolso;

    @MockitoBean
    private ConsultarHistorialDeComprasUseCase consultarHistorial;

    private Compra compraPendiente() {
        return Compra.realizar("id-1", "rep-1", "ana",
                Precio.enPesos(new BigDecimal("15000")), LocalDateTime.of(2026, 10, 1, 10, 0));
    }

    @Test
    void deberiaCrearLaCompraYResponder201UsandoElCompradorDelToken() throws Exception {
        // Arrange
        when(realizarCompra.ejecutar(any(), any(), any(), any())).thenReturn(compraPendiente());

        // Act & Assert
        mockMvc.perform(post("/api/compras")
                        .principal(() -> "ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUERPO_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        verify(realizarCompra).ejecutar(any(), eq("ana"), eq("rep-1"), any());
    }

    @Test
    void deberiaResponder404CuandoElRepuestoNoExiste() throws Exception {
        // Arrange
        when(realizarCompra.ejecutar(any(), any(), any(), any()))
                .thenThrow(new RecursoNoEncontradoException("No existe el repuesto indicado."));

        // Act & Assert
        mockMvc.perform(post("/api/compras")
                        .principal(() -> "ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUERPO_VALIDO))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe el repuesto indicado."));
    }

    @Test
    void deberiaResponder422CuandoSeRompeUnaReglaDeNegocio() throws Exception {
        // Arrange
        when(realizarCompra.ejecutar(any(), any(), any(), any()))
                .thenThrow(new ReglaDominioException("El comprador ya tiene una compra activa de este repuesto."));

        // Act & Assert
        mockMvc.perform(post("/api/compras")
                        .principal(() -> "ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUERPO_VALIDO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensaje").value("El comprador ya tiene una compra activa de este repuesto."));
    }

    @Test
    void noDeberiaLlamarAlCasoDeUsoSiFaltaElRepuesto() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/compras")
                        .principal(() -> "ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").exists());

        verify(realizarCompra, never()).ejecutar(any(), any(), any(), any());
    }

    @Test
    void deberiaConfirmarElPagoYResponder200() throws Exception {
        // Arrange
        Compra completada = compraPendiente();
        completada.completar();
        when(confirmarPago.ejecutar("id-1")).thenReturn(completada);

        // Act & Assert
        mockMvc.perform(put("/api/compras/id-1/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADA"));
    }

    @Test
    void deberiaRegistrarLaDescarga() throws Exception {
        // Arrange
        Compra completada = compraPendiente();
        completada.completar();
        completada.registrarDescarga(LocalDateTime.of(2026, 10, 2, 9, 0));
        when(registrarDescarga.ejecutar(eq("id-1"), any())).thenReturn(completada);

        // Act & Assert
        mockMvc.perform(put("/api/compras/id-1/descarga"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fechaDescarga").exists());
    }

    @Test
    void deberiaSolicitarElReembolsoConSuMotivo() throws Exception {
        // Arrange
        Compra reembolsada = compraPendiente();
        reembolsada.completar();
        reembolsada.solicitarReembolso("No era compatible", LocalDateTime.of(2026, 10, 2, 9, 0));
        when(solicitarReembolso.ejecutar(eq("id-1"), eq("No era compatible"), any())).thenReturn(reembolsada);

        // Act & Assert
        mockMvc.perform(put("/api/compras/id-1/reembolso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "motivo": "No era compatible" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("REEMBOLSADA"));
    }

    @Test
    void noDeberiaSolicitarElReembolsoSinMotivo() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/compras/id-1/reembolso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "motivo": "" }
                                """))
                .andExpect(status().isBadRequest());

        verify(solicitarReembolso, never()).ejecutar(any(), any(), any());
    }

    @Test
    void deberiaResponder422SiElReembolsoRompeUnaRegla() throws Exception {
        // Arrange
        when(solicitarReembolso.ejecutar(any(), any(), any()))
                .thenThrow(new ReglaDominioException("El plazo de reembolso ya vencio."));

        // Act & Assert
        mockMvc.perform(put("/api/compras/id-1/reembolso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "motivo": "Ya no lo quiero" }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensaje").value("El plazo de reembolso ya vencio."));
    }

    @Test
    void deberiaListarMisComprasUsandoElCompradorDelToken() throws Exception {
        // Arrange
        when(consultarHistorial.ejecutar("ana")).thenReturn(List.of(compraPendiente()));

        // Act & Assert
        mockMvc.perform(get("/api/compras/mias").principal(() -> "ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].compraId").value("id-1"))
                .andExpect(jsonPath("$[0].compradorId").value("ana"));
    }
}
