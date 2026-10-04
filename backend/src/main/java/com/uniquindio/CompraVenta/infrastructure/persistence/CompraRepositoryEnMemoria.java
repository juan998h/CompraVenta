package com.uniquindio.CompraVenta.infrastructure.persistence;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CompraRepositoryEnMemoria implements CompraRepository {

    private final Map<String, Compra> compras = new HashMap<>();

    @Override
    public Optional<Compra> obtenerPorId(String id) {
        return Optional.ofNullable(compras.get(id));
    }

    @Override
    public void guardar(Compra compra) {
        compras.put(compra.getId(), compra);
    }

    @Override
    public boolean existeCompraActiva(String compradorId, String repuestoId) {
        return compras.values().stream()
                .anyMatch(c -> c.getCompradorId().equals(compradorId)
                        && c.getRepuestoId().equals(repuestoId)
                        && c.estaActiva());
    }
}