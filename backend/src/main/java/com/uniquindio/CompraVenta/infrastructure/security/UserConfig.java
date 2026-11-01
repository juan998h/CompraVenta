package com.uniquindio.CompraVenta.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class UserConfig {

    // Le ensena a Spring Security a buscar un usuario por su correo
    @Bean
    public UserDetailsService userDetailsService(UsuarioJpaRepository repositorio) {
        return correo -> repositorio.findByCorreo(correo)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no existe"));
    }
}
