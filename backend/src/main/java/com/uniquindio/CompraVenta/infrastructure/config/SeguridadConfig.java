package com.uniquindio.CompraVenta.infrastructure.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
public class SeguridadConfig {

    // Aqui se define quien puede entrar a cada ruta
    @Bean
    public SecurityFilterChain filtroDeSeguridad(HttpSecurity http) throws Exception {

        // Sin token (o token malo): responde 401
        AuthenticationEntryPoint sinSesion = (request, response, error) ->
                responderConError(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Debe iniciar sesion con un token valido para usar este recurso.");

        // Con token pero sin el rol correcto: responde 403
        AccessDeniedHandler sinPermiso = (request, response, error) ->
                responderConError(response, HttpServletResponse.SC_FORBIDDEN,
                        "Su rol no tiene permiso para usar este recurso.");

        http
                // CSRF se desactiva porque la API usa tokens y no sesiones de navegador
                .csrf(csrf -> csrf.disable())
                // Sin sesiones: cada peticion trae su token
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(rutas -> rutas

                        // Rutas publicas: registro, login y Swagger
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Solo el VENDEDOR publica y elimina repuestos
                        .requestMatchers(HttpMethod.POST, "/api/repuestos").hasAuthority("VENDEDOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/repuestos/*").hasAuthority("VENDEDOR")

                        // Solo el ADMIN verifica vendedores y confirma pagos
                        .requestMatchers(HttpMethod.PUT, "/api/vendedores/*/verificar").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/compras/*/confirmar").hasAuthority("ADMIN")

                        // Solo el COMPRADOR compra, ve su historial, descarga y pide reembolso
                        .requestMatchers(HttpMethod.POST, "/api/compras").hasAuthority("COMPRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/compras/mias").hasAuthority("COMPRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/compras/*/descarga").hasAuthority("COMPRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/compras/*/reembolso").hasAuthority("COMPRADOR")

                        // Los reportes los ve el VENDEDOR o el ADMIN
                        .requestMatchers("/api/reportes/**").hasAnyAuthority("VENDEDOR", "ADMIN")

                        // Todo lo demas pide estar autenticado
                        .anyRequest().authenticated())
                .exceptionHandling(errores -> errores
                        .authenticationEntryPoint(sinSesion)
                        .accessDeniedHandler(sinPermiso))
                // Revisa el token JWT que llega en cada peticion
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(convertidorDeRoles()))
                        .authenticationEntryPoint(sinSesion)
                        .accessDeniedHandler(sinPermiso));

        return http.build();
    }

    // Escribe el error en formato JSON: fecha, codigo y mensaje
    private void responderConError(HttpServletResponse response, int codigo, String mensaje) throws IOException {
        response.setStatus(codigo);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"timestamp\":\"" + LocalDateTime.now()
                + "\",\"status\":" + codigo
                + ",\"mensaje\":\"" + mensaje + "\"}");
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