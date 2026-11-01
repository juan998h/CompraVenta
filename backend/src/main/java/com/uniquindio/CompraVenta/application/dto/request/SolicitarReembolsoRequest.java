package com.uniquindio.CompraVenta.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SolicitarReembolsoRequest(
        @NotBlank(message = "El motivo es obligatorio") String motivo) {
}
