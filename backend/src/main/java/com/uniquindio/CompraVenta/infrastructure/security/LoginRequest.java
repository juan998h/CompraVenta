package com.uniquindio.CompraVenta.infrastructure.security;

import jakarta.validation.constraints.NotBlank;

/** El username es el correo del usuario. */
public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio") String username,
        @NotBlank(message = "La contrasena es obligatoria") String password) {
}
