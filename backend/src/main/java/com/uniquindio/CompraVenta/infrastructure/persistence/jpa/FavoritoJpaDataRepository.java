package com.uniquindio.CompraVenta.infrastructure.persistence.jpa;

import com.uniquindio.CompraVenta.infrastructure.persistence.jpa.entity.FavoritoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface FavoritoJpaDataRepository extends JpaRepository<FavoritoEntity, Long> {

    boolean existsByUsuarioIdAndRepuestoId(String usuarioId, String repuestoId);

    void deleteByUsuarioIdAndRepuestoId(String usuarioId, String repuestoId);

    List<FavoritoEntity> findByUsuarioIdOrderByIdDesc(String usuarioId);
}
