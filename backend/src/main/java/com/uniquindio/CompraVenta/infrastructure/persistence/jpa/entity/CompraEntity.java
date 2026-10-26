package com.uniquindio.CompraVenta.infrastructure.persistence.jpa.entity;

import com.uniquindio.CompraVenta.domain.valueobject.EstadoCompra;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "compras")
@Getter @Setter
@NoArgsConstructor
public class CompraEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "repuesto_id", nullable = false, length = 50)
    private String repuestoId;

    @Column(name = "comprador_id", nullable = false, length = 50)
    private String compradorId;

    @Embedded
    private PrecioEmbeddable precio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCompra estado;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime fechaCompra;

    @Column(name = "fecha_descarga")
    private LocalDateTime fechaDescarga;
}