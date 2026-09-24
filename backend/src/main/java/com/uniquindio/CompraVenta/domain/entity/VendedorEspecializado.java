package com.uniquindio.CompraVenta.domain.entity;

import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.valueobject.Especialidad;

import java.util.Objects;
import java.util.UUID;

public class VendedorEspecializado {

    private final String id;
    private final String nombre;
    private final Especialidad especialidad;
    private boolean verificado;

    public VendedorEspecializado(String nombre, Especialidad especialidad) {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaDominioException("El vendedor debe tener un nombre.");
        }
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.verificado = false;
    }

    public void verificarEspecialidad() {
        this.verificado = true;
    }

    public boolean puedePublicarEnCategoriaAltoRiesgo() {
        return verificado;
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