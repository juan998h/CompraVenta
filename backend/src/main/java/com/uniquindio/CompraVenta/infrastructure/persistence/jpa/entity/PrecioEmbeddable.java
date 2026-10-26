package com.uniquindio.CompraVenta.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PrecioEmbeddable {

    @Column(name = "precio_monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "precio_moneda", nullable = false, length = 3)
    private String moneda;
}