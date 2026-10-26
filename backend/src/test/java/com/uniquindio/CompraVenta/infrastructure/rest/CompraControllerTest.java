package com.uniquindio.CompraVenta.infrastructure.rest;

import com.uniquindio.CompraVenta.application.usecase.ConfirmarPagoUseCase;
import com.uniquindio.CompraVenta.application.usecase.RealizarCompraUseCase;
import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompraController.class)
@AutoConfigureMockMvc(addFilters = false)
class CompraControllerTest {

    private static final String CUERPO_VALIDO = """
            { "repuestoId": "rep-1", "compradorId": "ana" }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RealizarCompraUseCase realizarCompra;

    @MockitoBean
    private ConfirmarPagoUseCase confirmarPago;

    @Test
    void deberiaCrearLaCompraYResponder201ConLocation() throws Exception {
        // Arrange
        Compra compra = Compra.realizar("id-1", "rep-1", "ana",
                Precio.enPesos(new BigDecimal("15000")), LocalDateTime.of(2026, 10, 1, 10, 0));
        when(realizarCompra.ejecutar(any(), any(), any(), any())).thenReturn(compra);

        // Act & Assert
        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUERPO_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void deberiaResponder404CuandoElRepuestoNoExiste() throws Exception {
        // Arrange
        when(realizarCompra.ejecutar(any(), any(), any(), any()))
                .thenThrow(new RecursoNoEncontradoException("No existe el repuesto indicado."));

        // Act & Assert
        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUERPO_VALIDO))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe el repuesto indicado."));
    }

    @Test
    void deberiaResponder400CuandoSeRompeUnaReglaDeNegocio() throws Exception {
        // Arrange
        when(realizarCompra.ejecutar(any(), any(), any(), any()))
                .thenThrow(new ReglaDominioException("El comprador ya tiene una compra activa de este repuesto."));

        // Act & Assert
        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUERPO_VALIDO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El comprador ya tiene una compra activa de este repuesto."));
    }

    @Test
    void noDeberiaLlamarAlCasoDeUsoSiFaltaElRepuesto() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "compradorId": "ana" }
                                """))
                .andExpect(status().isBadRequest());

        verify(realizarCompra, never()).ejecutar(any(), any(), any(), any());
    }
}