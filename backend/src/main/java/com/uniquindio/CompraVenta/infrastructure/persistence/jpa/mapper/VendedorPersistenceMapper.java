package com.uniquindio.CompraVenta.infrastructure.persistence.jpa.mapper;

import com.uniquindio.CompraVenta.domain.entity.VendedorEspecializado;
import com.uniquindio.CompraVenta.infrastructure.persistence.jpa.entity.VendedorEntity;
import org.springframework.stereotype.Component;

@Component
public class VendedorPersistenceMapper {

    public VendedorEntity toEntity(VendedorEspecializado vendedor) {
        VendedorEntity entity = new VendedorEntity();
        entity.setId(vendedor.getId());
        entity.setNombre(vendedor.getNombre());
        entity.setEspecialidad(vendedor.getEspecialidad());
        entity.setVerificado(vendedor.isVerificado());
        return entity;
    }

    public VendedorEspecializado toDomain(VendedorEntity entity) {
        return VendedorEspecializado.reconstruir(
                entity.getId(),
                entity.getNombre(),
                entity.getEspecialidad(),
                entity.isVerificado());
    }
}
