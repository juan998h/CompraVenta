package com.uniquindio.CompraVenta.domain.valueobject;

import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;

public record Garantia(int mesesCobertura, String condicionesFabricante) {

    public Garantia {
        if (mesesCobertura < 0) {
            throw new ReglaDominioException("Los meses de cobertura no pueden ser negativos.");
        }
    }
}