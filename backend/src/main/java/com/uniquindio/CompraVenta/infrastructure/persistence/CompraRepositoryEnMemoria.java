package com.uniquindio.CompraVenta.infrastructure.persistence;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import com.uniquindio.CompraVenta.domain.repository.ReembolsosPorComprador;
import com.uniquindio.CompraVenta.domain.repository.RepuestoMasVendido;
import com.uniquindio.CompraVenta.domain.valueobject.EstadoCompra;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public List<Compra> obtenerPorComprador(String compradorId) {
        return compras.values().stream()
                .filter(c -> c.getCompradorId().equals(compradorId))
                .sorted(Comparator.comparing(Compra::getFechaCompra).reversed())
                .toList();
    }

    @Override
    public boolean existeCompraActivaDelRepuesto(String repuestoId) {
        return compras.values().stream()
                .anyMatch(c -> c.getRepuestoId().equals(repuestoId)
                        && c.estaActiva());
    }

    @Override
    public List<ReembolsosPorComprador> compradoresConMasReembolsos(int limite) {
        return compras.values().stream()
                .filter(c -> c.getEstado() == EstadoCompra.REEMBOLSADA)
                .collect(Collectors.groupingBy(Compra::getCompradorId, Collectors.counting()))
                .entrySet().stream()
                .map(e -> new ReembolsosPorComprador(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(ReembolsosPorComprador::cantidad).reversed()
                        .thenComparing(ReembolsosPorComprador::compradorId))
                .limit(limite)
                .toList();
    }

    @Override
    public List<RepuestoMasVendido> repuestosMasVendidos(int limite) {
        // En memoria no hay tabla de repuestos: el "nombre" es el mismo id.
        return compras.values().stream()
                .filter(c -> c.getEstado() == EstadoCompra.COMPLETADA)
                .collect(Collectors.groupingBy(Compra::getRepuestoId, Collectors.counting()))
                .entrySet().stream()
                .map(e -> new RepuestoMasVendido(e.getKey(), e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(RepuestoMasVendido::unidades).reversed()
                        .thenComparing(RepuestoMasVendido::nombre))
                .limit(limite)
                .toList();
    }
}
