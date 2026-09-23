package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;

public class RealizarCompraUseCase {

    // TODO: inyectar RepuestoRepository y CompraRepository

    public void ejecutar(String compradorId, Repuesto repuesto) {
        if (compradorId == null || compradorId.isBlank()) {
            throw new ReglaDominioException("Debe indicarse el comprador que realiza la compra.");
        }
        if (repuesto.isEliminadoLogicamente()) {
            throw new ReglaDominioException("No se puede comprar un repuesto que ya no está disponible.");
        }
        // TODO: crear y guardar la Compra en estado PENDIENTE
    }
}