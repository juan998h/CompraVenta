package com.uniquindio.CompraVenta.infrastructure.persistence.jpa.entity;

import com.uniquindio.CompraVenta.domain.valueobject.Especialidad;
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
@Table(name = "vendedores")
@Getter @Setter
@NoArgsConstructor
public class VendedorEntity {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Especialidad especialidad;

    @Column(nullable = false)
    private boolean verificado;
}
