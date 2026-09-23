package com.uniquindio.CompraVenta.domain.valueobject;

import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;

public record Cilindraje(int centimetrosCubicos) {

    private static final int MINIMO_ALTO_CILINDRAJE = 400;

    public Cilindraje {
        if (centimetrosCubicos <= 0) {
            throw new ReglaDominioException("El cilindraje debe ser mayor a cero.");
        }
    }

    public boolean esAltoCilindraje() {
        return centimetrosCubicos >= MINIMO_ALTO_CILINDRAJE;
    }
}