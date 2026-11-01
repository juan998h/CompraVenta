package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.repository.Pagina;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;

import java.math.BigDecimal;

public class ListarRepuestosUseCase {

    public static final int TAMANO_MAXIMO = 50;

    private final RepuestoRepository repository;

    public ListarRepuestosUseCase(RepuestoRepository repository) {
        this.repository = repository;
    }

    public Pagina<Repuesto> ejecutar(String texto, BigDecimal precioMaximo, int pagina, int tamano) {
        // Validar la entrada es trabajo del caso de uso y filtrar y contar es trabajo de la base de datos.
        if (pagina < 0) {
            throw new IllegalArgumentException("La pagina no puede ser negativa.");
        }
        if (tamano < 1 || tamano > TAMANO_MAXIMO) {
            throw new IllegalArgumentException("El tamano de la pagina debe estar entre 1 y " + TAMANO_MAXIMO + ".");
        }
        if (precioMaximo != null && precioMaximo.signum() < 0) {
            throw new IllegalArgumentException("El precio maximo no puede ser negativo.");
        }
        return repository.buscarDisponibles(texto, precioMaximo, pagina, tamano);
    }
}
