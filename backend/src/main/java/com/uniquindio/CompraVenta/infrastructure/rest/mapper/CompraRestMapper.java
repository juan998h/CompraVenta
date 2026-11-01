package com.uniquindio.CompraVenta.infrastructure.rest.mapper;

import com.uniquindio.CompraVenta.application.dto.response.CompraResponse;
import com.uniquindio.CompraVenta.domain.entity.Compra;
import org.springframework.stereotype.Component;

@Component
public class CompraRestMapper {

    public CompraResponse aResponse(Compra compra) {
        return new CompraResponse(
                compra.getId(),
                compra.getRepuestoId(),
                compra.getCompradorId(),
                compra.getPrecioPagado().monto(),
                compra.getPrecioPagado().moneda(),
                compra.getEstado(),
                compra.getFechaCompra(),
                compra.getFechaDescarga());
    }
}
