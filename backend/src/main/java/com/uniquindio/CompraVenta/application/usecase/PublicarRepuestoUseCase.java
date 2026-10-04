package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.entity.VendedorEspecializado;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;

import java.math.BigDecimal;

public class PublicarRepuestoUseCase {

    private final RepuestoRepository repository;

    public PublicarRepuestoUseCase(RepuestoRepository repository) {
        this.repository = repository;
    }

    public Repuesto ejecutar(String repuestoId, VendedorEspecializado vendedor, String nombre,
                             BigDecimal precio, Compatibilidad compatibilidad, Garantia garantia,
                             boolean esCategoriaAltoRiesgo) {
        Repuesto repuesto = vendedor.publicarRepuesto(repuestoId, nombre, precio,
                compatibilidad, garantia, esCategoriaAltoRiesgo);
        repository.guardar(repuesto);
        return repuesto;
    }
}