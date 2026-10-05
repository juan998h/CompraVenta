package com.uniquindio.CompraVenta.infrastructure.persistence;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;

import java.util.HashMap;
import java.util.List;
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

    @Override
    public List<Repuesto> obtenerDisponiblesCompatiblesCon(Modelo modelo) {
        return repuestos.values().stream()
                .filter(r -> !r.isEliminadoLogicamente())
                .filter(r -> r.esCompatibleCon(modelo))
                .toList();