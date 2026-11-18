package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.CompraVenta.domain.exception.RecursoYaExisteException;
import com.uniquindio.CompraVenta.domain.repository.FavoritoRepository;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;

public class AgregarFavoritoUseCase {

    private final FavoritoRepository favoritoRepository;
    private final RepuestoRepository repuestoRepository;

    public AgregarFavoritoUseCase(
            FavoritoRepository favoritoRepository,
            RepuestoRepository repuestoRepository) {
        this.favoritoRepository = favoritoRepository;
        this.repuestoRepository = repuestoRepository;
    }

    public void ejecutar(String usuarioId, String repuestoId) {
        Repuesto repuesto = repuestoRepository.obtenerPorId(repuestoId)
            .orElseThrow(() -> new RecursoNoEncontradoException(
                "No existe el repuesto indicado."
            ));

        if (repuesto.isEliminadoLogicamente()) {
            throw new RecursoNoEncontradoException(
                "No existe el repuesto indicado."
            );
        }

        if (favoritoRepository.existe(usuarioId, repuestoId)) {
            throw new RecursoYaExisteException(
                "Ese repuesto ya esta en tus favoritos."
            );
        }

        favoritoRepository.agregar(usuarioId, repuestoId);
    }
}
