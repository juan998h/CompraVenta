package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;

import java.util.List;

public class BuscarRepuestosCompatiblesUseCase {

    private final RepuestoRepository repository;

    public BuscarRepuestosCompatiblesUseCase(RepuestoRepository repository) {
        this.repository = repository;
    }

    public List<Repuesto> ejecutar(Modelo modelo) {
        return repository.obtenerDisponiblesCompatiblesCon(modelo);
    }
}