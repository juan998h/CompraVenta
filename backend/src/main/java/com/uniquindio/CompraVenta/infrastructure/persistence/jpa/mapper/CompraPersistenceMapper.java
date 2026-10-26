package com.uniquindio.CompraVenta.infrastructure.persistence.jpa.mapper;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import com.uniquindio.CompraVenta.infrastructure.persistence.jpa.entity.CompraEntity;
import com.uniquindio.CompraVenta.infrastructure.persistence.jpa.entity.PrecioEmbeddable;
import org.springframework.stereotype.Component;

@Component
public class CompraPersistenceMapper {

    public CompraEntity toEntity(Compra compra) {
        CompraEntity entity = new CompraEntity();
        entity.setId(compra.getId());
        entity.setRepuestoId(compra.getRepuestoId());
        entity.setCompradorId(compra.getCompradorId());
        entity.setPrecio(new PrecioEmbeddable(
                compra.getPrecioPagado().monto(),
                compra.getPrecioPagado().moneda()));
        entity.setEstado(compra.getEstado());
        entity.setFechaCompra(compra.getFechaCompra());
        entity.setFechaDescarga(compra.getFechaDescarga());
        return entity;
    }

    public Compra toDomain(CompraEntity entity) {
        return Compra.reconstruir(
                entity.getId(),
                entity.getRepuestoId(),
                entity.getCompradorId(),
                new Precio(entity.getPrecio().getMonto(), entity.getPrecio().getMoneda()),
                entity.getFechaCompra(),
                entity.getEstado(),
                entity.getFechaDescarga());
    }
}