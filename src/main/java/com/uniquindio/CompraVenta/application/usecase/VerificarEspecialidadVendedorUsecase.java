package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.VendedorEspecializado;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;

/**
 * Caso de uso: Verificar Especialidad del Vendedor.
 */
public class VerificarEspecialidadVendedorUseCase {

    // TODO: inyectar VendedorRepository para persistir el cambio de estado

    public void ejecutar(VendedorEspecializado vendedor) {
        if (vendedor == null) {
            throw new ReglaDominioException("Debe indicarse un vendedor para verificar.");
        }
        if (vendedor.isVerificado()) {
            throw new ReglaDominioException("Este vendedor ya se encuentra verificado.");
        }
        // TODO: validar credenciales reales (certificado, portafolio, etc.) antes de verificar
        vendedor.verificarEspecialidad();
        // TODO: guardar el vendedor actualizado a través del repositorio
    }
}