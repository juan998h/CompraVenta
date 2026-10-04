package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;

import java.time.LocalDateTime;

public class RegistrarDescargaUseCase {

    private final CompraRepository repository;

    public RegistrarDescargaUseCase(CompraRepository repository) {
        this.repository = repository;
    }

    public Compra ejecutar(String compraId, LocalDateTime ahora) {
        Compra compra = repository.obtenerPorId(compraId)
                .orElseThrow(() -> new ReglaDominioException("No existe la compra indicada."));
        compra.registrarDescarga(ahora);
        repository.guardar(compra);
        return compra;
    }
}