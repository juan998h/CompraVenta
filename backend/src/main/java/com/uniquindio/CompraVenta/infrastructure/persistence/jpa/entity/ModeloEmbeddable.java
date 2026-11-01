package com.uniquindio.CompraVenta.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Un modelo de moto con el que es compatible un repuesto. */
@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ModeloEmbeddable {

    @Column(name = "marca", nullable = false, length = 60)
    private String marca;

    @Column(name = "version_modelo", nullable = false, length = 60)
    private String versionModelo;

    @Column(name = "anio", nullable = false)
    private int anio;

    @Column(name = "cilindraje_cc", nullable = false)
    private int cilindrajeCc;
}
