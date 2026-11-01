package com.uniquindio.CompraVenta.infrastructure.persistence.jpa;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import com.uniquindio.CompraVenta.domain.repository.ReembolsosPorComprador;
import com.uniquindio.CompraVenta.domain.repository.RepuestoMasVendido;
import com.uniquindio.CompraVenta.domain.valueobject.EstadoCompra;
import com.uniquindio.CompraVenta.infrastructure.persistence.jpa.mapper.CompraPersistenceMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class CompraRepositoryJpa implements CompraRepository {

    private static final Set<EstadoCompra> ESTADOS_ACTIVOS =
            EnumSet.of(EstadoCompra.PENDIENTE, EstadoCompra.COMPLETADA);

    private final CompraJpaDataRepository dataRepository;
    private final CompraPersistenceMapper mapper;

    public CompraRepositoryJpa(CompraJpaDataRepository dataRepository,
                               CompraPersistenceMapper mapper) {
        this.dataRepository = dataRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Compra> obtenerPorId(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void guardar(Compra compra) {
        dataRepository.save(mapper.toEntity(compra));
    }

    @Override
    public boolean existeCompraActiva(String compradorId, String repuestoId) {
        return dataRepository.existsByCompradorIdAndRepuestoIdAndEstadoIn(
                compradorId, repuestoId, ESTADOS_ACTIVOS);
    }

    @Override
    public boolean existeCompraActivaDelRepuesto(String repuestoId) {
        return dataRepository.existsByRepuestoIdAndEstadoIn(repuestoId, ESTADOS_ACTIVOS);
    }

    @Override
    public List<ReembolsosPorComprador> compradoresConMasReembolsos(int limite) {
        return dataRepository.compradoresConMasReembolsos(
                EstadoCompra.REEMBOLSADA, PageRequest.of(0, limite));
    }

    @Override
    public List<RepuestoMasVendido> repuestosMasVendidos(int limite) {
        return dataRepository.repuestosMasVendidos(
                EstadoCompra.COMPLETADA, PageRequest.of(0, limite));
    }
}
