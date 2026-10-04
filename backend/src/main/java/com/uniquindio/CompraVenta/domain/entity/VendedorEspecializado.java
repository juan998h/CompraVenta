package com.uniquindio.CompraVenta.domain.entity;

import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Especialidad;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;

import java.math.BigDecimal;
import java.util.Objects;

public class VendedorEspecializado {

    private final String id;
    private final String nombre;
    private final Especialidad especialidad;
    private boolean verificado;

    private VendedorEspecializado(String id, String nombre, Especialidad especialidad) {
        if (id == null || id.isBlank()) {
            throw new ReglaDominioException("El vendedor debe tener un identificador.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaDominioException("El vendedor debe tener un nombre.");
        }
        if (especialidad == null) {
            throw new ReglaDominioException("El vendedor debe declarar una especialidad.");
        }
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.verificado = false;
    }

    public static VendedorEspecializado registrar(String id, String nombre, Especialidad especialidad) {
        return new VendedorEspecializado(id, nombre, especialidad);
    }

    public void verificarEspecialidad() {
        if (this.verificado) {
            throw new ReglaDominioException("Este vendedor ya se encuentra verificado.");
        }
        this.verificado = true;
    }

    public boolean puedePublicarEnCategoriaAltoRiesgo() {
        return verificado;
    }

    public Repuesto publicarRepuesto(String repuestoId, String nombre, BigDecimal precio,
                                     Compatibilidad compatibilidad, Garantia garantia,
                                     boolean esCategoriaAltoRiesgo) {
        if (esCategoriaAltoRiesgo && !verificado) {
            throw new ReglaDominioException("El vendedor no esta verificado para publicar en esta categoria.");
        }
        return Repuesto.publicar(repuestoId, this.id, nombre, precio, compatibilidad, garantia);
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public Especialidad getEspecialidad() { return especialidad; }
    public boolean isVerificado() { return verificado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VendedorEspecializado that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}