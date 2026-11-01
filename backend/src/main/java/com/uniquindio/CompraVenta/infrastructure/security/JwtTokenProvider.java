package com.uniquindio.CompraVenta.infrastructure.security;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;


@Component
public class JwtTokenProvider {

    private final JwtEncoder encoder;

    public JwtTokenProvider(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String generar(String usuarioId, String correo, Collection<String> roles,
                          Instant emitidoEn, Instant expiraEn) {
        // HEADER: dice con que formula se firma
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        // PAYLOAD: lo que dice el token. El subject es el id del usuario: asi el sistema sabe
        // quien es el comprador o el vendedor sin que lo envie en el cuerpo de la peticion.
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("bikersdreams-api")
                .issuedAt(emitidoEn)
                .expiresAt(expiraEn)
                .subject(usuarioId)
                .claim("correo", correo)
                .claim("roles", roles)
                .build();

        // Sella: calcula la firma con la clave secreta -> header.payload.firma
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
