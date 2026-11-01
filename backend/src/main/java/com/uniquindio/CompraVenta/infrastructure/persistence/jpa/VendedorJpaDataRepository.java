package com.uniquindio.CompraVenta.infrastructure.persistence.jpa;

import com.uniquindio.CompraVenta.infrastructure.persistence.jpa.entity.VendedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

interface VendedorJpaDataRepository extends JpaRepository<VendedorEntity, String> {
}
