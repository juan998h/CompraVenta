package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;
import com.uniquindio.CompraVenta.domain.service.ServicioDeEliminacionDeRepuestos;

public class EliminarRepuestoUseCase {

    private final RepuestoRepository repuestoRepository;
    private final ServicioDeEliminacionDeRepuestos servicio;

    public EliminarRepuestoUseCase(RepuestoRepository repuestoRepository,
                                   ServicioDeEliminacionDeRepuestos servicio) {
        this.repuestoRepository = repuestoRepository;
        this.servicio = servicio;
    }

    public Repuesto ejecutar(String repuestoId) {
        Repuesto repuesto = repuestoRepository.obtenerPorId(repuestoId)
                .orElseThrow(() -> new ReglaDominioException("No existe el repuesto indicado."));
        servicio.eliminar(repuesto);
        repuestoRepository.guardar(repuesto);
        return repuesto;
    }
}