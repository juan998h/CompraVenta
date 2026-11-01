package com.uniquindio.CompraVenta.infrastructure.persistence.jpa;

import com.uniquindio.CompraVenta.domain.entity.VendedorEspecializado;
import com.uniquindio.CompraVenta.domain.repository.VendedorRepository;
import com.uniquindio.CompraVenta.infrastructure.persistence.jpa.mapper.VendedorPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class VendedorRepositoryJpa implements VendedorRepository {

    private final VendedorJpaDataRepository dataRepository;
    private final VendedorPersistenceMapper mapper;

    public VendedorRepositoryJpa(VendedorJpaDataRepository dataRepository,
                                 VendedorPersistenceMapper mapper) {
        this.dataRepository = dataRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<VendedorEspecializado> obtenerPorId(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void guardar(VendedorEspecializado vendedor) {
        dataRepository.save(mapper.toEntity(vendedor));
    }
}
