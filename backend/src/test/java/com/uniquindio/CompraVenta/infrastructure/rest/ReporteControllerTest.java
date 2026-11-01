package com.uniquindio.CompraVenta.infrastructure.rest;

import com.uniquindio.CompraVenta.application.usecase.ConsultarRepuestosMasVendidosUseCase;
import com.uniquindio.CompraVenta.domain.repository.RepuestoMasVendido;
import com.uniquindio.CompraVenta.infrastructure.rest.mapper.RepuestoRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReporteController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(RepuestoRestMapper.class)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarRepuestosMasVendidosUseCase repuestosMasVendidos;

    @Test
    void deberiaDevolverElRankingConElLimitePorDefecto() throws Exception {
        // Arrange
        when(repuestosMasVendidos.ejecutar(5)).thenReturn(List.of(
                new RepuestoMasVendido("pastillas-freno", "Pastillas de freno deportivas", 4L),
                new RepuestoMasVendido("escape-deportivo", "Escape deportivo de acero", 3L)));

        // Act & Assert
        mockMvc.perform(get("/api/reportes/repuestos-mas-vendidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].repuestoId").value("pastillas-freno"))
                .andExpect(jsonPath("$[0].unidadesVendidas").value(4))
                .andExpect(jsonPath("$[1].nombre").value("Escape deportivo de acero"));
    }

    @Test
    void deberiaResponder400SiElLimiteEsInvalido() throws Exception {
        // Arrange
        when(repuestosMasVendidos.ejecutar(0))
                .thenThrow(new IllegalArgumentException("El limite debe estar entre 1 y 20."));

        // Act & Assert
        mockMvc.perform(get("/api/reportes/repuestos-mas-vendidos").param("limite", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El limite debe estar entre 1 y 20."));
    }
}
