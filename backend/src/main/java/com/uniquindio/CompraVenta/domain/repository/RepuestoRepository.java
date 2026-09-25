package com.uniquindio.CompraVenta.domain.repository;
import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import java.util.Optional;

public interface RepuestoRepository {
    Optional<Repuesto> obtenerPorId(String id);
    void guardar(Repuesto repuesto);
}