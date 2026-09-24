
package com.uniquindio.CompraVenta.infrastructure.persistence;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.Repository.RepuestoRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RepuestoRepositoryEnMemoria implements RepuestoRepository {

    private final Map<String, Repuesto> repuestos = new HashMap<>();

    @Override
    public Optional<Repuesto> obtenerPorId(String id) {
        return Optional.ofNullable(repuestos.get(id));
    }

    @Override
    public void guardar(Repuesto repuesto) {
        repuestos.put(repuesto.getId(), repuesto);
    }
}