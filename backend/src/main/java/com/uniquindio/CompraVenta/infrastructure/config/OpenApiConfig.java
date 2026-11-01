package com.uniquindio.CompraVenta.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Datos que se ven arriba en Swagger UI (http://localhost:8080/swagger-ui.html). */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI documentacionDeLaApi() {
        return new OpenAPI().info(new Info()
                .title("Bikers Dreams API")
                .description("Marketplace de repuestos para motocicletas de alto cilindraje (400 cc o mas).")
                .version("1.0.0"));
    }
}
