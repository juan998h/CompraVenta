package com.uniquindio.CompraVenta.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RealizarCompraRequest(
        @NotBlank(message = "El repuesto es obligatorio") String repuestoId,
        @NotBlank(message = "El comprador es obligatorio") String compradorId) {
}