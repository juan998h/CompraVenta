package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;

import java.time.LocalDateTime;

public class SolicitarReembolsoUseCase {

    private final CompraRepository repository;

    public SolicitarReembolsoUseCase(CompraRepository repository) {
        this.repository = repository;
    }

    public Compra ejecutar(String compraId, String motivo, LocalDateTime ahora) {
        Compra compra = repository.obtenerPorId(compraId)
                .orElseThrow(() -> new ReglaDominioException("No existe la compra indicada."));
        compra.solicitarReembolso(motivo, ahora);
        repository.guardar(compra);
        return compra;
    }
}