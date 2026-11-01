package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import com.uniquindio.CompraVenta.domain.repository.RepuestoMasVendido;

import java.util.List;

public class ConsultarRepuestosMasVendidosUseCase {

    public static final int LIMITE_MAXIMO = 20;

    private final CompraRepository repository;

    public ConsultarRepuestosMasVendidosUseCase(CompraRepository repository) {
        this.repository = repository;
    }

    public List<RepuestoMasVendido> ejecutar(int limite) {
        if (limite < 1 || limite > LIMITE_MAXIMO) {
            throw new IllegalArgumentException("El limite debe estar entre 1 y " + LIMITE_MAXIMO + ".");
        }
        return repository.repuestosMasVendidos(limite);
    }
}
