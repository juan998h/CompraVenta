package com.uniquindio.CompraVenta.infrastructure.persistence.jpa;

import com.uniquindio.CompraVenta.domain.repository.ReembolsosPorComprador;
import com.uniquindio.CompraVenta.domain.valueobject.EstadoCompra;
import com.uniquindio.CompraVenta.infrastructure.persistence.jpa.entity.CompraEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

interface CompraJpaDataRepository extends JpaRepository<CompraEntity, String> {

    boolean existsByCompradorIdAndRepuestoIdAndEstadoIn(
            String compradorId, String repuestoId, Collection<EstadoCompra> estados);

    boolean existsByRepuestoIdAndEstadoIn(String repuestoId, Collection<EstadoCompra> estados);

    @Query("""
            SELECT new com.uniquindio.CompraVenta.domain.repository.ReembolsosPorComprador(c.compradorId, COUNT(c))
            FROM CompraEntity c
            WHERE c.estado = :estado
            GROUP BY c.compradorId
            ORDER BY COUNT(c) DESC, c.compradorId ASC
            """)
    List<ReembolsosPorComprador> compradoresConMasReembolsos(
            @Param("estado") EstadoCompra estado, Pageable pageable);
}