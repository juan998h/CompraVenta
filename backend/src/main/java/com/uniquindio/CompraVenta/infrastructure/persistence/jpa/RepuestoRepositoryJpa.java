package com.uniquindio.CompraVenta.infrastructure.persistence.jpa;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.repository.Pagina;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;
import com.uniquindio.CompraVenta.infrastructure.persistence.jpa.entity.RepuestoEntity;
import com.uniquindio.CompraVenta.infrastructure.persistence.jpa.mapper.RepuestoPersistenceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion real del puerto RepuestoRepository, que vive en domain.
 */
@Repository
public class RepuestoRepositoryJpa implements RepuestoRepository {

    private final RepuestoJpaDataRepository dataRepository;
    private final RepuestoPersistenceMapper mapper;

    public RepuestoRepositoryJpa(RepuestoJpaDataRepository dataRepository,
                                 RepuestoPersistenceMapper mapper) {
        this.dataRepository = dataRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Repuesto> obtenerPorId(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void guardar(Repuesto repuesto) {
        dataRepository.save(mapper.toEntity(repuesto));
    }

    @Override
    public List<Repuesto> obtenerDisponiblesCompatiblesCon(Modelo modelo) {
        return dataRepository
                .buscarDisponiblesCompatibles(modelo.marca(), modelo.version(), modelo.anio())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Pagina<Repuesto> buscarDisponibles(String texto, BigDecimal precioMaximo,
                                              int pagina, int tamano) {
        Page<RepuestoEntity> resultado = dataRepository.buscarDisponibles(
                texto == null ? "" : texto,
                precioMaximo,
                PageRequest.of(pagina, tamano, Sort.by("nombre")));
        List<Repuesto> contenido = resultado.getContent().stream()
                .map(mapper::toDomain)
                .toList();
        return new Pagina<>(contenido, resultado.getTotalElements(),
                resultado.getTotalPages(), pagina, tamano);
    }
}
