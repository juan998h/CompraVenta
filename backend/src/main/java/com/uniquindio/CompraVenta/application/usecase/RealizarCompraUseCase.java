package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;

// El flujo típico: alguien encuentra un repuesto que le sirve y lo compra.
// Por ahora solo validamos lo básico antes de crear la compra en sí.
public class RealizarCompraUseCase {

    public void ejecutar(String compradorId, Repuesto repuesto) {
        if (compradorId == null || compradorId.isBlank()) {
            throw new ReglaDominioException("Debe indicarse el comprador que realiza la compra.");
        }
        if (repuesto.isEliminadoLogicamente()) {
            // si el vendedor ya lo dio de baja, no debería poder comprarse
            throw new ReglaDominioException("No se puede comprar un repuesto que ya no está disponible.");
        }

    }
}