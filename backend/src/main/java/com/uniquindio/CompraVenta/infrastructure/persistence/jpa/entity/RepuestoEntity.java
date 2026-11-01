package com.uniquindio.CompraVenta.infrastructure.persistence.jpa.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA de Repuesto. NO es la entidad del dominio: esta clase solo sabe de tablas
 * y columnas. La conversion con el dominio la hace RepuestoPersistenceMapper.
 * La eliminacion es logica: la fila nunca se borra, solo se marca eliminado = true.
 */
@Entity
@Table(name = "repuestos")
@Getter @Setter
@NoArgsConstructor
public class RepuestoEntity {

    @Id
    @Column(length = 50)
    private String id;

    @Column(name = "vendedor_id", nullable = false, length = 50)
    private String vendedorId;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Embedded
    private PrecioEmbeddable precio;

    @Embedded
    private GarantiaEmbeddable garantia;

    @Column(nullable = false)
    private boolean eliminado;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "repuesto_modelos",
            joinColumns = @JoinColumn(name = "repuesto_id"))
    private List<ModeloEmbeddable> modelos = new ArrayList<>();
}
