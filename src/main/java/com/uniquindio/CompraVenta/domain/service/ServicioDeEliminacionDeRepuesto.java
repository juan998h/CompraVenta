package com.uniquindio.CompraVenta.domain.service;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;

public class ServicioDeEliminacionDeRepuestos {

    private final CompraRepository compraRepository;

    public ServicioDeEliminacionDeRepuestos(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    public void eliminar(Repuesto repuesto) {
        if (repuesto == null) {
            throw new ReglaDominioException("Debe indicarse el repuesto a eliminar.");
        }
        if (compraRepository.existeCompraActivaDelRepuesto(repuesto.getId())) {
            throw new ReglaDominioException("No se puede eliminar un repuesto con compras activas.");
        }
        repuesto.eliminarLogicamente();
    }
}