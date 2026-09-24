package com.uniquindio.CompraVenta.domain.service;

import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.valueobject.Cilindraje;

import java.math.BigDecimal;

public class ServicioCalculoImpuesto {

    public BigDecimal calcular(Cilindraje cilindraje, String ciudad) {
        if (ciudad == null || ciudad.isBlank()) {
            throw new ReglaDominioException("Debe declararse una ciudad para calcular el impuesto.");
        }
        BigDecimal tarifaBase = cilindraje.esAltoCilindraje()
                ? new BigDecimal("450000")
                : new BigDecimal("200000");
        BigDecimal factorCiudad = obtenerFactorCiudad(ciudad);
        return tarifaBase.multiply(factorCiudad);
    }

    private BigDecimal obtenerFactorCiudad(String ciudad) {
        return switch (ciudad.toLowerCase()) {
            case "bogotá", "bogota" -> new BigDecimal("1.15");
            case "medellín", "medellin" -> new BigDecimal("1.10");
            default -> BigDecimal.ONE;
        };
    }
}