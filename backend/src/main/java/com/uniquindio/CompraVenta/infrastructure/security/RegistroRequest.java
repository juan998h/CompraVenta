package com.uniquindio.CompraVenta.infrastructure.security;

import com.uniquindio.CompraVenta.domain.valueobject.Especialidad;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** La especialidad solo se pide cuando el rol es VENDEDOR. El rol ADMIN no se puede auto-registrar. */
public record RegistroRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es valido") String correo,
        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 8, message = "La contrasena debe tener al menos 8 caracteres")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).*$",
                message = "La contrasena debe tener al menos una mayuscula y un numero") String contrasena,
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotNull(message = "El rol es obligatorio") RolUsuario rol,
        Especialidad especialidad) {
}
