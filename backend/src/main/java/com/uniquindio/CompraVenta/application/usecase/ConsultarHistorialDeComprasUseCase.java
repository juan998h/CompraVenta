package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;

import java.util.List;

public class ConsultarHistorialDeComprasUseCase {

    private final CompraRepository repository;

    public ConsultarHistorialDeComprasUseCase(CompraRepository repository) {
        this.repository = repository;
    }

    public List<Compra> ejecutar(String compradorId) {
        if (compradorId == null || compradorId.isBlank()) {
            throw new IllegalArgumentException("Debe indicarse el comprador.");
        }
        return repository.obtenerPorComprador(compradorId);
    }
}
