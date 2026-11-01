package com.uniquindio.CompraVenta.infrastructure.security;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, String> {

    Optional<UsuarioEntity> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
}
