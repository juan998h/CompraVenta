package com.uniquindio.CompraVenta.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ModeloRequest(
        @NotBlank(message = "La marca es obligatoria") String marca,
        @NotBlank(message = "La version es obligatoria") String version,
        @Min(value = 1900, message = "El año no es valido") int anio,
        @Min(value = 1, message = "El cilindraje debe ser mayor a cero") int cilindrajeCc) {
}
