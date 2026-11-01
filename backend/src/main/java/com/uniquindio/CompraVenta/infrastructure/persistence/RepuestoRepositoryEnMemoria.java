package com.uniquindio.CompraVenta.infrastructure.persistence;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.repository.Pagina;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;

import java.math.BigDecimal;
import java.util.Comparator;
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
    }

    @Override
    public Pagina<Repuesto> buscarDisponibles(String texto, BigDecimal precioMaximo,
                                              int pagina, int tamano) {
        String filtro = texto == null ? "" : texto.toLowerCase();
        List<Repuesto> coincidencias = repuestos.values().stream()
                .filter(r -> !r.isEliminadoLogicamente())
                .filter(r -> r.getNombre().toLowerCase().contains(filtro))
                .filter(r -> precioMaximo == null
                        || r.getPrecio().monto().compareTo(precioMaximo) <= 0)
                .sorted(Comparator.comparing(Repuesto::getNombre))
                .toList();
        int desde = Math.min(pagina * tamano, coincidencias.size());
        int hasta = Math.min(desde + tamano, coincidencias.size());
        int totalPaginas = (int) Math.ceil(coincidencias.size() / (double) tamano);
        return new Pagina<>(coincidencias.subList(desde, hasta), coincidencias.size(),
                totalPaginas, pagina, tamano);
    }
}
