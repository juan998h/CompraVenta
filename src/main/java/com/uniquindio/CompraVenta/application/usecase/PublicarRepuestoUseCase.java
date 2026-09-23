package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.entity.VendedorEspecializado;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;

import java.math.BigDecimal;

/**
 * Caso de uso: Publicar Repuesto.
 * Un VendedorEspecializado publica un nuevo Repuesto en el catálogo.
 *
 * Reglas de negocio que aplica:
 * - El repuesto debe declarar compatibilidad con al menos un modelo (Repuesto lo valida en su constructor).
 * - TODO: si la categoría es de alto riesgo, exigir vendedor.puedePublicarEnCategoriaAltoRiesgo().
 */
public class PublicarRepuestoUseCase {

    // TODO: inyectar RepuestoRepository

    public Repuesto ejecutar(VendedorEspecializado vendedor, String nombre, BigDecimal precio,
                             Compatibilidad compatibilidad, Garantia garantia) {
        Repuesto repuesto = new Repuesto(nombre, precio, compatibilidad, garantia);
        // TODO: guardar el repuesto a través del repositorio
        return repuesto;
    }
}