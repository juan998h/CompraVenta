package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.VendedorEspecializado;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;

// Este es el caso de uso donde un vendedor deja de ser "nuevo" y pasa a estar
// verificado. Sin esto, nadie podría publicar mapas de ECU ni nada de alto riesgo,
// así que es literalmente la puerta de entrada al negocio.
public class VerificarEspecialidadVendedorUseCase {

    // falta conectar esto con un repositorio real para guardar el cambio en la BD

    public void ejecutar(VendedorEspecializado vendedor) {
        if (vendedor == null) {
            throw new ReglaDominioException("Debe indicarse un vendedor para verificar.");
        }
        if (vendedor.isVerificado()) {
            throw new ReglaDominioException("Este vendedor ya se encuentra verificado.");
        }
        vendedor.verificarEspecialidad();

    }
}