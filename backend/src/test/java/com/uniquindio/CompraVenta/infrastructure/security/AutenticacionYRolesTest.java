package com.uniquindio.CompraVenta.infrastructure.security;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Pruebas de punta a punta de la seguridad: login real, token real, rutas protegidas por rol. */
@SpringBootTest
@AutoConfigureMockMvc
class AutenticacionYRolesTest {

    @Autowired
    private MockMvc mockMvc;

    private String loginYObtenerToken(String correo, String contrasena) throws Exception {
        MvcResult resultado = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + correo + "\",\"password\":\"" + contrasena + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(resultado.getResponse().getContentAsString(), "$.token");
    }

    @Test
    void loginConCredencialesCorrectasDevuelveTokenYRol() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"anamaria1996@bikers.com\",\"password\":\"Comprador123*\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.roles[0]").value("COMPRADOR"));
    }

    @Test
    void loginConContrasenaIncorrectaResponde401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"anamaria1996@bikers.com\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginConCorreoInexistenteResponde401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"nadie@bikers.com\",\"password\":\"Comprador123*\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginSinCamposResponde400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registroDeCompradorNuevoResponde201() throws Exception {
        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"nuevo@bikers.com\",\"contrasena\":\"Clave12345\","
                                + "\"nombre\":\"Nuevo\",\"rol\":\"COMPRADOR\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("COMPRADOR"))
                .andExpect(jsonPath("$.contrasena").doesNotExist());
    }

    @Test
    void registroConCorreoRepetidoResponde422() throws Exception {
        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"anamaria1996@bikers.com\",\"contrasena\":\"Clave12345\","
                                + "\"nombre\":\"Otra Ana\",\"rol\":\"COMPRADOR\"}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void registroConContrasenaSinMayusculaNiNumeroResponde400() throws Exception {
        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"debil@bikers.com\",\"contrasena\":\"soloMinusculas\","
                                + "\"nombre\":\"Debil\",\"rol\":\"COMPRADOR\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void noSePuedeRegistrarUnAdministrador() throws Exception {
        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"falso@bikers.com\",\"contrasena\":\"Clave12345\","
                                + "\"nombre\":\"Falso\",\"rol\":\"ADMIN\"}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void rutaProtegidaSinTokenResponde401ConJson() throws Exception {
        mockMvc.perform(get("/api/compras/mias"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void tokenInventadoResponde401() throws Exception {
        mockMvc.perform(get("/api/compras/mias")
                        .header("Authorization", "Bearer esto.no.es-un-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void compradorNoPuedeVerificarVendedoresResponde403ConJson() throws Exception {
        String token = loginYObtenerToken("anamaria1996@bikers.com", "Comprador123*");

        mockMvc.perform(put("/api/vendedores/vendedor2/verificar")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void vendedorNoPuedeComprarResponde403() throws Exception {
        String token = loginYObtenerToken("motopartsSAS@bikers.com", "Vendedor123*");

        mockMvc.perform(post("/api/compras")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"repuestoId\":\"filtro-aceite\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminPuedeVerificarAUnVendedor() throws Exception {
        String token = loginYObtenerToken("admin2020@bikers.com", "Admin123*");

        mockMvc.perform(put("/api/vendedores/vendedor2/verificar")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void elTokenDeUnaCompradoraDejaVerSuHistorial() throws Exception {
        String token = loginYObtenerToken("anamaria1996@bikers.com", "Comprador123*");

        MvcResult resultado = mockMvc.perform(get("/api/compras/mias")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        assertFalse(resultado.getResponse().getContentAsString().isBlank());
    }
}
