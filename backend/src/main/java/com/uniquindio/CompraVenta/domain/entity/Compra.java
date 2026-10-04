package com.uniquindio.CompraVenta.domain.entity;

import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.valueobject.EstadoCompra;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;

import java.time.LocalDateTime;
import java.util.Objects;

public class Compra {

    public static final int PLAZO_REEMBOLSO_DIAS = 5;

    private final String id;
    private final String repuestoId;
    private final String compradorId;
    private final Precio precioPagado;
    private final LocalDateTime fechaCompra;
    private EstadoCompra estado;
    private LocalDateTime fechaDescarga;

    private Compra(String id, String repuestoId, String compradorId,
                   Precio precioPagado, LocalDateTime fechaCompra) {
        if (id == null || id.isBlank()) {
            throw new ReglaDominioException("La compra debe tener un identificador.");
        }
        if (repuestoId == null || repuestoId.isBlank()) {
            throw new ReglaDominioException("La compra debe referenciar un repuesto.");
        }
        if (compradorId == null || compradorId.isBlank()) {
            throw new ReglaDominioException("La compra debe referenciar un comprador.");
        }
        if (precioPagado == null) {
            throw new ReglaDominioException("La compra debe registrar el precio pagado.");
        }
        if (fechaCompra == null) {
            throw new ReglaDominioException("La compra debe tener fecha.");
        }
        this.id = id;
        this.repuestoId = repuestoId;
        this.compradorId = compradorId;
        this.precioPagado = precioPagado;
        this.fechaCompra = fechaCompra;
        this.estado = EstadoCompra.PENDIENTE;
        this.fechaDescarga = null;
    }

    public static Compra realizar(String id, String repuestoId, String compradorId,
                                  Precio precioPagado, LocalDateTime fechaCompra) {
        return new Compra(id, repuestoId, compradorId, precioPagado, fechaCompra);
    }

    public void completar() {
        if (estado != EstadoCompra.PENDIENTE) {
            throw new ReglaDominioException("Solo una compra pendiente puede completarse.");
        }
        this.estado = EstadoCompra.COMPLETADA;
    }

    public void registrarDescarga(LocalDateTime ahora) {
        if (estado != EstadoCompra.COMPLETADA) {
            throw new ReglaDominioException("Solo una compra completada puede descargarse.");
        }
        if (ahora == null) {
            throw new ReglaDominioException("La descarga debe tener fecha.");
        }
        if (fechaDescarga == null) {
            this.fechaDescarga = ahora;
        }
    }

    public void solicitarReembolso(String motivo, LocalDateTime ahora) {
        if (estado != EstadoCompra.COMPLETADA) {
            throw new ReglaDominioException("Solo una compra completada puede reembolsarse.");
        }
        if (fechaDescarga != null) {
            throw new ReglaDominioException("No se puede reembolsar una compra cuyo archivo ya fue descargado.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new ReglaDominioException("El reembolso debe tener un motivo.");
        }
        if (ahora.isAfter(fechaCompra.plusDays(PLAZO_REEMBOLSO_DIAS))) {
            throw new ReglaDominioException("El plazo de reembolso ya vencio.");
        }
        this.estado = EstadoCompra.REEMBOLSADA;
    }

    public boolean estaActiva() {
        return estado == EstadoCompra.PENDIENTE || estado == EstadoCompra.COMPLETADA;
    }

    public boolean fueDescargada() {
        return fechaDescarga != null;
    }

    public String getId() { return id; }
    public String getRepuestoId() { return repuestoId; }
    public String getCompradorId() { return compradorId; }
    public Precio getPrecioPagado() { return precioPagado; }
    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public LocalDateTime getFechaDescarga() { return fechaDescarga; }
    public EstadoCompra getEstado() { return estado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Compra that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}