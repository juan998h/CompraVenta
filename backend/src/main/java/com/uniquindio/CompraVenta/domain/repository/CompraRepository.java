package com.uniquindio.CompraVenta.domain.repository;

import com.uniquindio.CompraVenta.domain.entity.Compra;

import java.util.Optional;

public interface CompraRepository {
    Optional<Compra> obtenerPorId(String id);
    void guardar(Compra compra);
    boolean existeCompraActiva(String compradorId, String repuestoId);
}