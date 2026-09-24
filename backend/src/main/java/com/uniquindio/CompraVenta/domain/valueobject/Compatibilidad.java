package com.uniquindio.CompraVenta.domain.valueobject;

import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;

public record Modelo(String marca, String version, int anio, Cilindraje cilindraje) {

    public Modelo {
        if (marca == null || marca.isBlank()) {
            throw new ReglaDominioException("El modelo debe tener una marca.");
        }
        if (version == null || version.isBlank()) {
            throw new ReglaDominioException("El modelo debe tener una versión.");
        }
    }
}