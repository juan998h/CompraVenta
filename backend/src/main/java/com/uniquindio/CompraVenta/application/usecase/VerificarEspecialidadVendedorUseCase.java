package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.VendedorEspecializado;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.repository.VendedorRepository;

public class VerificarEspecialidadVendedorUseCase {

    private final VendedorRepository repository;

    public VerificarEspecialidadVendedorUseCase(VendedorRepository repository) {
        this.repository = repository;
    }

    public VendedorEspecializado ejecutar(String vendedorId) {
        VendedorEspecializado vendedor = repository.obtenerPorId(vendedorId)
                .orElseThrow(() -> new ReglaDominioException("No existe el vendedor indicado."));
        vendedor.verificarEspecialidad();
        repository.guardar(vendedor);
        return vendedor;
    }
}