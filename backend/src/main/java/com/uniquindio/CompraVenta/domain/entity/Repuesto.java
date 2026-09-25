package com.uniquindio.CompraVenta.domain.entity;

import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Repuesto {

    private final String id;
    private final String nombre;
    private final BigDecimal precio;
    private final Compatibilidad compatibilidad;
    private final Garantia garantia;
    private boolean eliminadoLogicamente;

    public Repuesto(String nombre, BigDecimal precio, Compatibilidad compatibilidad, Garantia garantia) {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaDominioException("El repuesto debe tener un nombre.");
        }
        if (precio == null || precio.signum() <= 0) {
            throw new ReglaDominioException("El precio debe ser mayor a cero.");
        }
        if (compatibilidad == null || compatibilidad.modelosCompatibles().isEmpty()) {
            throw new ReglaDominioException("Un repuesto debe declarar al menos un modelo compatible.");
        }
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.precio = precio;
        this.compatibilidad = compatibilidad;
        this.garantia = garantia;
        this.eliminadoLogicamente = false;
    }

    public boolean esCompatibleCon(Modelo modeloComprador) {
        return compatibilidad.incluyeModelo(modeloComprador);
    }

    public Garantia obtenerGarantia() {
        return garantia;
    }

    public void eliminarLogicamente() {
        if (this.eliminadoLogicamente) {
            throw new ReglaDominioException("Este repuesto ya fue eliminado.");
        }
        this.eliminadoLogicamente = true;
    }

    public void venderUnidad() {
        if (this.eliminadoLogicamente) {
            throw new ReglaDominioException("No se puede vender un repuesto que ya fue eliminado.");
        }
    }
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public boolean isEliminadoLogicamente() { return eliminadoLogicamente; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Repuesto that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}