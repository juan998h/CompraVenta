package com.uniquindio.CompraVenta.domain.entity;

import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.valueobject.EstadoCompra;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Raiz del agregado Compra.
 *
 * Invariantes que garantiza:
 *  - toda compra nace en estado PENDIENTE;
 *  - el precio pagado queda congelado al momento de comprar;
 *  - solo se transita entre estados permitidos por el ciclo de vida;
 *  - no se reembolsa fuera del plazo, sin motivo ni si el archivo ya fue descargado.
 */
public class Compra {

    public static final int PLAZO_REEMBOLSO_DIAS = 5;

    private final String id;
    private final String repuestoId;     // solo el id: Repuesto es OTRO agregado
    private final String compradorId;    // solo el id: no hay entidad Comprador
    private final Precio precioPagado;   // el precio "de una foto", no cambia despues
    private final LocalDateTime fechaCompra;
    private EstadoCompra estado;
    private LocalDateTime fechaDescarga;

    // Constructor privado: solo asigna. Las reglas se validan en realizar(...).
    private Compra(String id, String repuestoId, String compradorId, Precio precioPagado,
                   LocalDateTime fechaCompra, EstadoCompra estado, LocalDateTime fechaDescarga) {
        this.id = id;
        this.repuestoId = repuestoId;
        this.compradorId = compradorId;
        this.precioPagado = precioPagado;
        this.fechaCompra = fechaCompra;
        this.estado = estado;
        this.fechaDescarga = fechaDescarga;
    }

    // Unico punto de entrada para crear una Compra: valida ANTES de existir.
    public static Compra realizar(String id, String repuestoId, String compradorId,
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
        return new Compra(id, repuestoId, compradorId, precioPagado, fechaCompra,
                EstadoCompra.PENDIENTE, null);
    }

    // Solo para el adaptador de persistencia: "revive" una compra que YA existia (no valida).
    public static Compra reconstruir(String id, String repuestoId, String compradorId,
                                     Precio precioPagado, LocalDateTime fechaCompra,
                                     EstadoCompra estado, LocalDateTime fechaDescarga) {
        return new Compra(id, repuestoId, compradorId, precioPagado, fechaCompra,
                estado, fechaDescarga);
    }

    // ---------- Comportamiento del negocio ----------
    // Cada metodo valida PRIMERO y cambia el estado DESPUES.

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

    // ---------- Consultas ----------
    // Sin setters: solo lectura hacia afuera.

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