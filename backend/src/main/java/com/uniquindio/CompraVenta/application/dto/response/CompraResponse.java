package com.uniquindio.CompraVenta.application.dto.response;

import com.uniquindio.CompraVenta.domain.valueobject.EstadoCompra;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CompraResponse(String compraId, String repuestoId, String compradorId,
                             BigDecimal monto, String moneda, EstadoCompra estado,
                             LocalDateTime fechaCompra, LocalDateTime fechaDescarga) {
}
