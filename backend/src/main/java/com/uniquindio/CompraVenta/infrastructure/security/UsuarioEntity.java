package com.uniquindio.CompraVenta.infrastructure.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter @Setter
@NoArgsConstructor
public class UsuarioEntity {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, unique = true, length = 120)
    private String correo;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name = "contrasena_hash", nullable = false, length = 100)
    private String contrasenaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolUsuario rol;
}
