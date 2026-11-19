package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;

import java.util.List;

/** Compras en estado PENDIENTE, las que el administrador debe confirmar. */
public class ListarComprasPendientesUseCase {

    private final CompraRepository repository;

    public ListarComprasPendientesUseCase(CompraRepository repository) {
        this.repository = repository;
    }

    public List<Compra> ejecutar() {
        return repository.obtenerPendientes();
    }
}