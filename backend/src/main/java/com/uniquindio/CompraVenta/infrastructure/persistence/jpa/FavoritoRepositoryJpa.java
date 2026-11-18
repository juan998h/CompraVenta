package com.uniquindio.CompraVenta.infrastructure.persistence.jpa;

import com.uniquindio.CompraVenta.domain.repository.FavoritoRepository;
import com.uniquindio.CompraVenta.infrastructure.persistence.jpa.entity.FavoritoEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class FavoritoRepositoryJpa implements FavoritoRepository {

    private final FavoritoJpaDataRepository dataRepository;

    public FavoritoRepositoryJpa(FavoritoJpaDataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public boolean existe(String usuarioId, String repuestoId) {
        return dataRepository.existsByUsuarioIdAndRepuestoId(
            usuarioId, repuestoId
        );
    }

    @Override
    public void agregar(String usuarioId, String repuestoId) {
        FavoritoEntity favorito = new FavoritoEntity();
        favorito.setUsuarioId(usuarioId);
        favorito.setRepuestoId(repuestoId);
        dataRepository.save(favorito);
    }

    @Override
    @Transactional
    public void quitar(String usuarioId, String repuestoId) {
        dataRepository.deleteByUsuarioIdAndRepuestoId(usuarioId, repuestoId);
    }

    @Override
    public List<String> idsDeRepuestosFavoritos(String usuarioId) {
        return dataRepository.findByUsuarioIdOrderByIdDesc(usuarioId)
            .stream()
            .map(FavoritoEntity::getRepuestoId)
            .toList();
    }
}
