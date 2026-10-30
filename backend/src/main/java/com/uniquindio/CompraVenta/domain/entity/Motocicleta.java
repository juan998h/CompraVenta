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
    private final List<String> historialMantenimiento;

    // Constructor privado: solo asigna. Las reglas se validan en registrar(...).
    private Motocicleta(String id, Modelo modelo, int kilometraje,
                        List<String> historialMantenimiento) {
        this.id = id;
        this.modelo = modelo;
        this.kilometraje = kilometraje;
        this.historialMantenimiento = historialMantenimiento;
    }

    // Unico punto de entrada para registrar una Motocicleta: valida ANTES de existir.
    public static Motocicleta registrar(String id, Modelo modelo) {
        if (id == null || id.isBlank()) {
            throw new ReglaDominioException("La motocicleta debe tener placa o VIN.");
        }
        if (modelo == null) {
            throw new ReglaDominioException("La motocicleta debe tener un modelo.");
        }
        return new Motocicleta(id, modelo, 0, new ArrayList<>());
    }

    // Solo para el adaptador de persistencia: "revive" una motocicleta que YA existia (no valida).
    public static Motocicleta reconstruir(String id, Modelo modelo, int kilometraje,
                                          List<String> historialMantenimiento) {
        return new Motocicleta(id, modelo, kilometraje, new ArrayList<>(historialMantenimiento));
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