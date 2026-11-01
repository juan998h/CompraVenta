package com.uniquindio.CompraVenta.infrastructure.rest;

import com.uniquindio.CompraVenta.application.usecase.VerificarEspecialidadVendedorUseCase;
import com.uniquindio.CompraVenta.domain.entity.VendedorEspecializado;
import com.uniquindio.CompraVenta.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.valueobject.Especialidad;
import com.uniquindio.CompraVenta.infrastructure.rest.mapper.VendedorRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VendedorController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(VendedorRestMapper.class)
class VendedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VerificarEspecialidadVendedorUseCase verificarEspecialidad;

    @Test
    void deberiaVerificarAlVendedorYDevolverloVerificado() throws Exception {
        // Arrange
        VendedorEspecializado vendedor = VendedorEspecializado.registrar("vend-2", "Garage Racing", Especialidad.SUSPENSION);
        vendedor.verificarEspecialidad();
        when(verificarEspecialidad.ejecutar("vend-2")).thenReturn(vendedor);

        // Act & Assert
        mockMvc.perform(put("/api/vendedores/vend-2/verificar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vendedorId").value("vend-2"))
                .andExpect(jsonPath("$.verificado").value(true));
    }

    @Test
    void deberiaResponder404SiElVendedorNoExiste() throws Exception {
        // Arrange
        when(verificarEspecialidad.ejecutar("no-existe"))
                .thenThrow(new RecursoNoEncontradoException("No existe el vendedor indicado."));

        // Act & Assert
        mockMvc.perform(put("/api/vendedores/no-existe/verificar"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deberiaResponder422SiElVendedorYaEstabaVerificado() throws Exception {
        // Arrange
        when(verificarEspecialidad.ejecutar("vend-1"))
                .thenThrow(new ReglaDominioException("Este vendedor ya se encuentra verificado."));

        // Act & Assert
        mockMvc.perform(put("/api/vendedores/vend-1/verificar"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensaje").value("Este vendedor ya se encuentra verificado."));
    }
}
