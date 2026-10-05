package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;

public class ConfirmarPagoUseCase {

    private final CompraRepository repository;

    public ConfirmarPagoUseCase(CompraRepository repository) {
        this.repository = repository;
    }

    public Compra ejecutar(String compraId) {
        Compra compra = repository.obtenerPorId(compraId)
                .orElseThrow(() -> new ReglaDominioException("No existe la compra indicada."));
        compra.completar();
        repository.guardar(compra);
        return compra;
    }
}