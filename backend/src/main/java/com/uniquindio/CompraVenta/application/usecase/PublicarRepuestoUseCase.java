package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.entity.VendedorEspecializado;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;
import com.uniquindio.CompraVenta.domain.repository.VendedorRepository;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;

public class PublicarRepuestoUseCase {

    private final RepuestoRepository repuestoRepository;
    private final VendedorRepository vendedorRepository;

    public PublicarRepuestoUseCase(RepuestoRepository repuestoRepository,
                                   VendedorRepository vendedorRepository) {
        this.repuestoRepository = repuestoRepository;
        this.vendedorRepository = vendedorRepository;
    }

    public Repuesto ejecutar(String repuestoId, String vendedorId, String nombre, Precio precio,
                             Compatibilidad compatibilidad, Garantia garantia,
                             boolean esCategoriaAltoRiesgo) {
        VendedorEspecializado vendedor = vendedorRepository.obtenerPorId(vendedorId)
                .orElseThrow(() -> new ReglaDominioException("No existe el vendedor indicado."));
        Repuesto repuesto = vendedor.publicarRepuesto(repuestoId, nombre, precio,
                compatibilidad, garantia, esCategoriaAltoRiesgo);
        repuestoRepository.guardar(repuesto);
        return repuesto;
    }
}