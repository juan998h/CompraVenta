package com.uniquindio.CompraVenta.domain.service;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;

import java.time.LocalDateTime;

public class ServicioDeCompra {

    private final CompraRepository compraRepository;

    public ServicioDeCompra(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    public Compra realizarCompra(String compraId, String compradorId,
                                 Repuesto repuesto, LocalDateTime ahora) {
        if (repuesto == null) {
            throw new ReglaDominioException("Debe indicarse el repuesto a comprar.");
        }
        repuesto.venderUnidad();
        if (compraRepository.existeCompraActiva(compradorId, repuesto.getId())) {
            throw new ReglaDominioException("El comprador ya tiene una compra activa de este repuesto.");
        }
        return Compra.realizar(compraId, repuesto.getId(), compradorId, repuesto.getPrecio(), ahora);
    }
}