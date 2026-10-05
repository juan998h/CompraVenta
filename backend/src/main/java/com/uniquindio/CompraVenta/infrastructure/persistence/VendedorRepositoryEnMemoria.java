package com.uniquindio.CompraVenta.infrastructure.persistence;

import com.uniquindio.CompraVenta.domain.entity.VendedorEspecializado;
import com.uniquindio.CompraVenta.domain.repository.VendedorRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VendedorRepositoryEnMemoria implements VendedorRepository {

    private final Map<String, VendedorEspecializado> vendedores = new HashMap<>();

    @Override
    public Optional<VendedorEspecializado> obtenerPorId(String id) {
        return Optional.ofNullable(vendedores.get(id));
    }

    @Override
    public void guardar(VendedorEspecializado vendedor) {
        vendedores.put(vendedor.getId(), vendedor);
    }
}