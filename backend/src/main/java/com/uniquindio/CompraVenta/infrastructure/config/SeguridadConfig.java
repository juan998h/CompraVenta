package com.uniquindio.CompraVenta.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SeguridadConfig {

    // Aqui se define quien puede entrar a cada ruta
    @Bean
    public SecurityFilterChain filtroDeSeguridad(HttpSecurity http) throws Exception {
        http
                // CSRF se desactiva porque la API usa tokens y no sesiones de navegador
                .csrf(csrf -> csrf.disable())
                // Sin sesiones: cada peticion trae su token
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(rutas -> rutas
                        // Swagger se puede abrir sin token
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Solo el COMPRADOR puede comprar
                        .requestMatchers(HttpMethod.POST, "/api/compras").hasAuthority("COMPRADOR")
                        // Solo el ADMIN puede confirmar un pago
                        .requestMatchers(HttpMethod.PUT, "/api/compras/*/confirmar").hasAuthority("ADMIN")
                        // Lo demas pide estar autenticado
                        .anyRequest().authenticated())
                // Revisa el token JWT que llega en cada peticion
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(convertidorDeRoles())));
        return http.build();
    }

    // Lee los roles que trae el token (campo "roles") para usarlos en hasAuthority
    private JwtAuthenticationConverter convertidorDeRoles() {
        JwtGrantedAuthoritiesConverter autoridades = new JwtGrantedAuthoritiesConverter();
        autoridades.setAuthoritiesClaimName("roles");
        autoridades.setAuthorityPrefix("");

        JwtAuthenticationConverter convertidor = new JwtAuthenticationConverter();
        convertidor.setJwtGrantedAuthoritiesConverter(autoridades);
        return convertidor;
    }

    // Verifica la firma del token con la clave secreta que esta en application.properties
    @Bean
    public JwtDecoder jwtDecoder(@Value("${seguridad.jwt.secreto}") String secreto) {
        SecretKey llave = new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(llave).build();
    }
}