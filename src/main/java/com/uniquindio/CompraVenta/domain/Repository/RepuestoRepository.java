package com.uniquindio.CompraVenta.domain.Repository;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;

import java.util.List;
import java.util.Optional;

public interface RepuestoRepository {
    Optional<Repuesto> obtenerPorId(String id);
    void guardar(Repuesto repuesto);
    List<Repuesto> obtenerDisponiblesCompatiblesCon(Modelo modelo);
}