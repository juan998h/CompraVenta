package com.uniquindio.CompraVenta.domain.entity;

import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Raiz del agregado Repuesto.
 *
 * Invariantes que garantiza:
 *  - un repuesto siempre tiene id, vendedor, nombre, precio, garantia y al menos un modelo compatible;
 *  - un repuesto eliminado no se vende ni se modifica;
 *  - un repuesto eliminado no puede eliminarse otra vez;
 *  - solo el vendedor que lo publico puede eliminarlo.
 */
public class Repuesto {

    private final String id;
    private final String vendedorId;   // solo el id: VendedorEspecializado es OTRO agregado
    private String nombre;
    private Precio precio;
    private Compatibilidad compatibilidad;
    private Garantia garantia;
    private boolean eliminadoLogicamente;

    // Constructor privado: solo asigna. Las reglas se validan en publicar(...).
    private Repuesto(String id, String vendedorId, String nombre, Precio precio,
                     Compatibilidad compatibilidad, Garantia garantia,
                     boolean eliminadoLogicamente) {
        this.id = id;
        this.vendedorId = vendedorId;
        this.nombre = nombre;
        this.precio = precio;
        this.compatibilidad = compatibilidad;
        this.garantia = garantia;
        this.eliminadoLogicamente = eliminadoLogicamente;
    }

    // Unico punto de entrada para crear un Repuesto: valida ANTES de existir.
    public static Repuesto publicar(String id, String vendedorId, String nombre, Precio precio,
                                    Compatibilidad compatibilidad, Garantia garantia) {
        if (id == null || id.isBlank()) {
            throw new ReglaDominioException("El repuesto debe tener un identificador.");
        }
        if (vendedorId == null || vendedorId.isBlank()) {
            throw new ReglaDominioException("El repuesto debe pertenecer a un vendedor.");
        }
        validarNombre(nombre);
        validarPrecio(precio);
        validarCompatibilidad(compatibilidad);
        validarGarantia(garantia);
        return new Repuesto(id, vendedorId, nombre, precio, compatibilidad, garantia, false);
    }

    // Solo para el adaptador de persistencia: "revive" un repuesto que YA existia (no valida).
    public static Repuesto reconstruir(String id, String vendedorId, String nombre, Precio precio,
                                       Compatibilidad compatibilidad, Garantia garantia,
                                       boolean eliminadoLogicamente) {
        return new Repuesto(id, vendedorId, nombre, precio, compatibilidad, garantia,
                eliminadoLogicamente);
    }

    // ---------- Comportamiento del negocio ----------
    // Cada metodo valida PRIMERO y cambia el estado DESPUES.

    public void renombrar(String nuevoNombre) {
        verificarQueSePuedaModificar();
        validarNombre(nuevoNombre);
        this.nombre = nuevoNombre;
    }

    public void cambiarPrecio(Precio nuevoPrecio) {
        verificarQueSePuedaModificar();
        validarPrecio(nuevoPrecio);
        this.precio = nuevoPrecio;
    }

    public void actualizarGarantia(Garantia nuevaGarantia) {
        verificarQueSePuedaModificar();
        validarGarantia(nuevaGarantia);
        this.garantia = nuevaGarantia;
    }

    public void agregarModeloCompatible(Modelo modelo) {
        verificarQueSePuedaModificar();
        if (modelo == null) {
            throw new ReglaDominioException("Debe indicarse el modelo a agregar.");
        }
        if (compatibilidad.incluyeModelo(modelo)) {
            throw new ReglaDominioException("El repuesto ya es compatible con ese modelo.");
        }
        List<Modelo> modelos = new ArrayList<>(compatibilidad.modelosCompatibles());
        modelos.add(modelo);
        this.compatibilidad = new Compatibilidad(List.copyOf(modelos));
    }

    public void quitarModeloCompatible(Modelo modelo) {
        verificarQueSePuedaModificar();
        if (modelo == null) {
            throw new ReglaDominioException("Debe indicarse el modelo a quitar.");
        }
        if (!compatibilidad.incluyeModelo(modelo)) {
            throw new ReglaDominioException("El repuesto no es compatible con ese modelo.");
        }
        List<Modelo> restantes = compatibilidad.modelosCompatibles().stream()
                .filter(m -> !mismoModelo(m, modelo))
                .toList();
        if (restantes.isEmpty()) {
            throw new ReglaDominioException("Un repuesto debe declarar al menos un modelo compatible.");
        }
        this.compatibilidad = new Compatibilidad(restantes);
    }

    public void venderUnidad() {
        if (this.eliminadoLogicamente) {
            throw new ReglaDominioException("No se puede vender un repuesto que ya fue eliminado.");
        }
    }

    public void eliminarLogicamente() {
        if (this.eliminadoLogicamente) {
            throw new ReglaDominioException("Este repuesto ya fue eliminado.");
        }
        this.eliminadoLogicamente = true;
    }

    public void reactivar() {
        if (!this.eliminadoLogicamente) {
            throw new ReglaDominioException("Este repuesto no esta eliminado.");
        }
        this.eliminadoLogicamente = false;
    }

    public void verificarQueLoPuedeEliminar(String vendedorIdQueSolicita) {
        if (!perteneceA(vendedorIdQueSolicita)) {
            throw new ReglaDominioException("Solo el vendedor que publico el repuesto puede eliminarlo.");
        }
    }

    // ---------- Consultas ----------

    public boolean esCompatibleCon(Modelo modeloComprador) {
        return compatibilidad.incluyeModelo(modeloComprador);
    }

    public boolean estaDisponible() {
        return !eliminadoLogicamente;
    }

    public boolean perteneceA(String otroVendedorId) {
        return this.vendedorId.equals(otroVendedorId);
    }

    public boolean esDeAltoCilindraje() {
        return compatibilidad.modelosCompatibles().stream()
                .allMatch(m -> m.cilindraje() != null && m.cilindraje().esAltoCilindraje());
    }

    public Garantia obtenerGarantia() {
        return garantia;
    }

    // ---------- Verificaciones internas ----------
    // Privadas a proposito: nadie desde afuera puede saltarselas.

    private void verificarQueSePuedaModificar() {
        if (this.eliminadoLogicamente) {
            throw new ReglaDominioException("No se puede modificar un repuesto eliminado.");
        }
    }

    private static void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaDominioException("El repuesto debe tener un nombre.");
        }
    }

    private static void validarPrecio(Precio precio) {
        if (precio == null) {
            throw new ReglaDominioException("El repuesto debe tener un precio.");
        }
    }

    private static void validarCompatibilidad(Compatibilidad compatibilidad) {
        if (compatibilidad == null || compatibilidad.modelosCompatibles() == null
                || compatibilidad.modelosCompatibles().isEmpty()) {
            throw new ReglaDominioException("Un repuesto debe declarar al menos un modelo compatible.");
        }
    }

    private static void validarGarantia(Garantia garantia) {
        if (garantia == null) {
            throw new ReglaDominioException("Un repuesto debe tener una garantia.");
        }
    }

    private static boolean mismoModelo(Modelo a, Modelo b) {
        return a.marca().equalsIgnoreCase(b.marca())
                && a.version().equalsIgnoreCase(b.version())
                && a.anio() == b.anio();
    }

    // ---------- Getters ----------

    public String getId() { return id; }
    public String getVendedorId() { return vendedorId; }
    public String getNombre() { return nombre; }
    public Precio getPrecio() { return precio; }
    public Compatibilidad getCompatibilidad() { return compatibilidad; }
    public boolean isEliminadoLogicamente() { return eliminadoLogicamente; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Repuesto that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}