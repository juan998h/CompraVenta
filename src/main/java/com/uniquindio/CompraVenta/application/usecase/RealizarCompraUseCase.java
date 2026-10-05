package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;
import com.uniquindio.CompraVenta.domain.service.ServicioDeCompra;

import java.time.LocalDateTime;

public class RealizarCompraUseCase {

    private final RepuestoRepository repuestoRepository;
    private final CompraRepository compraRepository;
    private final ServicioDeCompra servicioDeCompra;

    public RealizarCompraUseCase(RepuestoRepository repuestoRepository,
                                 CompraRepository compraRepository,
                                 ServicioDeCompra servicioDeCompra) {
        this.repuestoRepository = repuestoRepository;
        this.compraRepository = compraRepository;
        this.servicioDeCompra = servicioDeCompra;
    }

    public Compra ejecutar(String compraId, String compradorId, String repuestoId, LocalDateTime ahora) {
        Repuesto repuesto = repuestoRepository.obtenerPorId(repuestoId)
                .orElseThrow(() -> new ReglaDominioException("No existe el repuesto indicado."));
        Compra compra = servicioDeCompra.realizarCompra(compraId, compradorId, repuesto, ahora);
        compraRepository.guardar(compra);
        return compra;
    }
}