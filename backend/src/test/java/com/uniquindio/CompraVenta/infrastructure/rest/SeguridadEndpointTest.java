package com.uniquindio.CompraVenta.infrastructure.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SeguridadEndpointTest {

    // C-004 es una compra PENDIENTE que viene en data.sql
    private static final String RUTA = "/api/compras/C-004/confirmar";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sinTokenDeberiaResponder401() throws Exception {
        mockMvc.perform(put(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void conRolIncorrectoDeberiaResponder403() throws Exception {
        mockMvc.perform(put(RUTA)
                        .with(jwt().authorities(new SimpleGrantedAuthority("COMPRADOR"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void conRolCorrectoDeberiaResponder200() throws Exception {
        mockMvc.perform(put(RUTA)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isOk());
    }
}