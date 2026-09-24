package com.uniquindio.CompraVenta.domain.valueobject;

import java.util.List;

public record Compatibilidad(List<Modelo> modelosCompatibles) {

    public boolean incluyeModelo(Modelo modelo) {
        return modelosCompatibles.stream()
                .anyMatch(m -> m.marca().equalsIgnoreCase(modelo.marca())
                        && m.version().equalsIgnoreCase(modelo.version())
                        && m.anio() == modelo.anio());
    }
}