package com.uniquindio.CompraVenta.domain.valueobject;

import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Precio(BigDecimal monto, String moneda) {

    public static final String MONEDA_POR_DEFECTO = "COP";

    public Precio {
        if (monto == null) {
            throw new ReglaDominioException("El precio debe tener un monto.");
        }
        monto = monto.setScale(2, RoundingMode.HALF_UP);
        if (monto.signum() <= 0) {
            throw new ReglaDominioException("El precio debe ser mayor a cero.");
        }
        if (moneda == null || moneda.isBlank()) {
            throw new ReglaDominioException("El precio debe tener una moneda.");
        }
        moneda = moneda.toUpperCase();
    }

    public static Precio enPesos(BigDecimal monto) {
        return new Precio(monto, MONEDA_POR_DEFECTO);
    }
}