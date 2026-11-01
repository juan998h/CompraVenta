package com.uniquindio.CompraVenta.application.dto.request;

import jakarta.validation.constraints.NotBlank;

/** El comprador NO viaja en el cuerpo: sale del token de quien hace la peticion. */
public record RealizarCompraRequest(
        @NotBlank(message = "El repuesto es obligatorio") String repuestoId) {
}
