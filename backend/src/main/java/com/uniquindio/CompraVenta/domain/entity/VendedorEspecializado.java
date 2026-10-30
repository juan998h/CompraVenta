package com.uniquindio.CompraVenta.domain.entity;

import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Especialidad;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;

import java.util.Objects;

public class VendedorEspecializado {

    private final String id;
    private final String nombre;
    private final Especialidad especialidad;
    private boolean verificado;

    // Constructor privado: solo asigna. Las reglas se validan en registrar(...).
    private VendedorEspecializado(String id, String nombre, Especialidad especialidad,
                                  boolean verificado) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.verificado = verificado;
    }

    // Unico punto de entrada para registrar un vendedor: valida ANTES de existir.
    public static VendedorEspecializado registrar(String id, String nombre, Especialidad especialidad) {
        if (id == null || id.isBlank()) {
            throw new ReglaDominioException("El vendedor debe tener un identificador.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaDominioException("El vendedor debe tener un nombre.");
        }
        if (especialidad == null) {
            throw new ReglaDominioException("El vendedor debe declarar una especialidad.");
        }
        return new VendedorEspecializado(id, nombre, especialidad, false);
    }

    // Solo para el adaptador de persistencia: "revive" un vendedor que YA existia (no valida).
    public static VendedorEspecializado reconstruir(String id, String nombre,
                                                    Especialidad especialidad, boolean verificado) {
        return new VendedorEspecializado(id, nombre, especialidad, verificado);
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

    public Repuesto publicarRepuesto(String repuestoId, String nombre, Precio precio,
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