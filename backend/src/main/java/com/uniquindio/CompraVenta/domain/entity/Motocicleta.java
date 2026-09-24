package com.uniquindio.CompraVenta.domain.entity;

import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Motocicleta {

    private final String id;
    private final Modelo modelo;
    private int kilometraje;
    private final List<String> historialMantenimiento = new ArrayList<>();

    public Motocicleta(String id, Modelo modelo) {
        if (id == null || id.isBlank()) {
            throw new ReglaDominioException("La motocicleta debe tener placa o VIN.");
        }
        this.id = id;
        this.modelo = modelo;
        this.kilometraje = 0;
    }

    public void registrarMantenimiento(String descripcion, int kilometrajeActual) {
        if (kilometrajeActual < this.kilometraje) {
            throw new ReglaDominioException("El kilometraje no puede disminuir.");
        }
        this.kilometraje = kilometrajeActual;
        this.historialMantenimiento.add(descripcion);
    }

    public List<String> consultarHistorial() {
        return List.copyOf(historialMantenimiento);
    }

    public String getId() { return id; }
    public Modelo getModelo() { return modelo; }
    public int getKilometraje() { return kilometraje; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Motocicleta that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}