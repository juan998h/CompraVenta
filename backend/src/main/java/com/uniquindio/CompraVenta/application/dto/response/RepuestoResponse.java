package com.uniquindio.CompraVenta.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record RepuestoResponse(String repuestoId, String vendedorId, String nombre,
              BigDecimal monto, String moneda, int mesesGarantia,
              List<ModeloResponse> modelosCompatibles) {
}
