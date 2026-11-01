package com.uniquindio.CompraVenta.infrastructure.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderTest {

    private static final String SECRETO = "clave-secreta-solo-para-la-prueba-1234567890";

    private JwtTokenProvider proveedor;
    private JwtDecoder decoder;
    private Instant ahora;

    @BeforeEach
    void preparar() {
        SecretKey llave = new SecretKeySpec(SECRETO.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        proveedor = new JwtTokenProvider(new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(llave)));
        decoder = NimbusJwtDecoder.withSecretKey(llave).build();
        ahora = Instant.now();
    }

    private String generarToken() {
        return proveedor.generar("ana", "ana@bikers.com", List.of("COMPRADOR"),
                ahora, ahora.plus(60, ChronoUnit.MINUTES));
    }

    @Test
    void elTokenLlevaElIdElCorreoElRolYLaFechaDeVencimiento() {
        // Act
        Jwt jwt = decoder.decode(generarToken());

        // Assert
        assertEquals("ana", jwt.getSubject());
        assertEquals("ana@bikers.com", jwt.getClaimAsString("correo"));
        assertEquals(List.of("COMPRADOR"), jwt.getClaimAsStringList("roles"));
        assertTrue(jwt.getExpiresAt().isAfter(Instant.now()));
    }

    @Test
    void elTokenNuncaLlevaLaContrasena() {
        // Act
        Jwt jwt = decoder.decode(generarToken());

        // Assert
        assertFalse(jwt.getClaims().containsKey("contrasena"));
        assertFalse(jwt.getClaims().containsKey("password"));
    }

    @Test
    void unTokenModificadoYaNoPasaLaVerificacionDeLaFirma() {
        // Arrange: se cambia una letra del payload (la parte de en medio) del token
        String[] partes = generarToken().split("\\.");
        char primera = partes[1].charAt(5);
        char cambiada = primera == 'A' ? 'B' : 'A';
        String adulterado = partes[0] + "." + partes[1].substring(0, 5) + cambiada
                + partes[1].substring(6) + "." + partes[2];

        // Act & Assert
        assertThrows(BadJwtException.class, () -> decoder.decode(adulterado));
    }
}
