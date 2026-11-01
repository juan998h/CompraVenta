package com.uniquindio.CompraVenta.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Datos de Swagger UI (http://localhost:8080/swagger-ui.html) y el boton Authorize:
 * se hace login en POST /api/auth/login, se copia el token y se pega en Authorize.
 */
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA = "bearerAuth";

    @Bean
    public OpenAPI documentacionDeLaApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bikers Dreams API")
                        .description("Marketplace de repuestos para motocicletas de alto cilindraje (400 cc o mas). "
                                + "Para las rutas protegidas inicie sesion en POST /api/auth/login y pegue el token en Authorize.")
                        .version("1.0.0"))
                .components(new Components().addSecuritySchemes(ESQUEMA,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT de /api/auth/login (solo el token, sin la palabra Bearer).")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA));
    }
}
