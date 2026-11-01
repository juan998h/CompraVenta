package com.uniquindio.CompraVenta.domain.repository;

import com.uniquindio.CompraVenta.domain.entity.Compra;

import java.util.List;
import java.util.Optional;

public interface CompraRepository {
    Optional<Compra> obtenerPorId(String id);
    void guardar(Compra compra);
    boolean existeCompraActiva(String compradorId, String repuestoId);
    boolean existeCompraActivaDelRepuesto(String repuestoId);
    List<ReembolsosPorComprador> compradoresConMasReembolsos(int limite);

    /** Ranking de repuestos por compras COMPLETADAS (une compras con repuestos). */
    List<RepuestoMasVendido> repuestosMasVendidos(int limite);
}