package com.uniquindio.CompraVenta.domain.repository;

import com.uniquindio.CompraVenta.domain.entity.VendedorEspecializado;

import java.util.Optional;

public interface VendedorRepository {
    Optional<VendedorEspecializado> obtenerPorId(String id);
    void guardar(VendedorEspecializado vendedor);
}