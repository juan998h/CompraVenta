package com.uniquindio.CompraVenta.domain.repository;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RepuestoRepository {
    Optional<Repuesto> obtenerPorId(String id);
    void guardar(Repuesto repuesto);
    List<Repuesto> obtenerDisponiblesCompatiblesCon(Modelo modelo);

    /**
     * Lista los repuestos NO eliminados cuyo nombre contiene el texto (si se indica)
     * y cuyo precio no supera el precio maximo (si se indica), ordenados por nombre.
     */
    Pagina<Repuesto> buscarDisponibles(String texto, BigDecimal precioMaximo, int pagina, int tamano);
}
