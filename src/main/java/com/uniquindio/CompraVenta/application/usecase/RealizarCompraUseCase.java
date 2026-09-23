package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;

/**
 * Caso de uso: Realizar Compra.
 * Un Comprador adquiere un Repuesto del catálogo.
 *
 * Reglas de negocio que aplica:
 * - No se puede comprar un repuesto eliminado lógicamente.
 * - TODO: no permitir compra duplicada activa del mismo repuesto (regla base del curso).
 */
public class RealizarCompraUseCase {

    // TODO: inyectar RepuestoRepository y CompraRepository

    public void ejecutar(String compradorId, Repuesto repuesto) {
        if (repuesto.isEliminadoLogicamente()) {
            throw new ReglaDominioException("No se puede comprar un repuesto que ya no está disponible.");
        }
        // TODO: crear y guardar la Compra en estado PENDIENTE
    }
}