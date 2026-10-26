package com.uniquindio.CompraVenta.infrastructure.rest;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;
import com.uniquindio.CompraVenta.domain.valueobject.Cilindraje;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.EstadoCompra;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ComprarRepuestoIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RepuestoRepository repuestoRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Test
    void deberiaComprarUnRepuestoYLuegoConfirmarElPago() throws Exception {
        // Arrange: un repuesto publicado y disponible
        Compatibilidad compatibilidad = new Compatibilidad(
                List.of(new Modelo("Yamaha", "MT-07", 2022, new Cilindraje(689))));
        repuestoRepository.guardar(Repuesto.publicar("rep-integracion", "vendedor-1",
                "Pastillas de freno", Precio.enPesos(new BigDecimal("150000")),
                compatibilidad, new Garantia(6, "Garantia de fabrica")));

        // Act 1: el comprador compra
        String ubicacion = mockMvc.perform(post("/api/compras")
                        .with(jwt().authorities(new SimpleGrantedAuthority("COMPRADOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "repuestoId": "rep-integracion", "compradorId": "ana" }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andReturn().getResponse().getHeader("Location");
        String compraId = ubicacion.substring(ubicacion.lastIndexOf('/') + 1);

        // Act 2: el administrador confirma el pago
        mockMvc.perform(put("/api/compras/" + compraId + "/confirmar")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADA"));

        // Assert: la base de datos quedo con la compra completada y con el precio congelado
        var guardada = compraRepository.obtenerPorId(compraId).orElseThrow();
        assertEquals(EstadoCompra.COMPLETADA, guardada.getEstado());
        assertEquals(new BigDecimal("150000.00"), guardada.getPrecioPagado().monto());
    }
}