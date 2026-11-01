package com.uniquindio.CompraVenta.infrastructure.rest.mapper;

import com.uniquindio.CompraVenta.application.dto.response.VendedorResponse;
import com.uniquindio.CompraVenta.domain.entity.VendedorEspecializado;
import org.springframework.stereotype.Component;

@Component
public class VendedorRestMapper {

    public VendedorResponse aResponse(VendedorEspecializado vendedor) {
        return new VendedorResponse(
                vendedor.getId(),
                vendedor.getNombre(),
                vendedor.getEspecialidad().name(),
                vendedor.isVerificado());
    }
}
