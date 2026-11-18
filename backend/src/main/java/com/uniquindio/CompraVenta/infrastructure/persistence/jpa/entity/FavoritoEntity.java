package com.uniquindio.CompraVenta.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "favoritos",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"usuario_id", "repuesto_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
public class FavoritoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false, length = 50)
    private String usuarioId;

    @Column(name = "repuesto_id", nullable = false, length = 50)
    private String repuestoId;
}
