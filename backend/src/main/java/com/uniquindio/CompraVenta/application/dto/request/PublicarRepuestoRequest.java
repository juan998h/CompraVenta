package com.uniquindio.CompraVenta.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

/** El vendedor NO viaja en el cuerpo: sale del token de quien hace la peticion. */
public record PublicarRepuestoRequest(
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero") BigDecimal monto,
        String moneda,
        @NotEmpty(message = "Debe indicarse al menos un modelo compatible")
        @Valid List<ModeloRequest> modelosCompatibles,
        @Min(value = 0, message = "Los meses de garantia no pueden ser negativos") int mesesGarantia,
        String condicionesFabricante,
        boolean esCategoriaAltoRiesgo) {
}
