package com.uniquindio.CompraVenta.infrastructure.persistence.jpa;

import com.uniquindio.CompraVenta.infrastructure.persistence.jpa.entity.RepuestoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

interface RepuestoJpaDataRepository extends JpaRepository<RepuestoEntity, String> {

    // Listado paginado: el filtro, el conteo y el corte de pagina los hace la base de datos.
    @Query("""
            SELECT r FROM RepuestoEntity r
            WHERE r.eliminado = false
              AND LOWER(r.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
              AND (:precioMaximo IS NULL OR r.precio.monto <= :precioMaximo)
            """)
    Page<RepuestoEntity> buscarDisponibles(@Param("texto") String texto,
                                           @Param("precioMaximo") BigDecimal precioMaximo,
                                           Pageable pageable);

    // Repuestos disponibles compatibles con un modelo (une repuestos con repuesto_modelos).
    @Query("""
            SELECT DISTINCT r FROM RepuestoEntity r JOIN r.modelos m
            WHERE r.eliminado = false
              AND LOWER(m.marca) = LOWER(:marca)
              AND LOWER(m.versionModelo) = LOWER(:version)
              AND m.anio = :anio
            """)
    List<RepuestoEntity> buscarDisponiblesCompatibles(@Param("marca") String marca,
                                                      @Param("version") String version,
                                                      @Param("anio") int anio);
}
