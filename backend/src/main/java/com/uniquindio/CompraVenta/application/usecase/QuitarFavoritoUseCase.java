package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.CompraVenta.domain.repository.FavoritoRepository;

public class QuitarFavoritoUseCase {

    private final FavoritoRepository favoritoRepository;

    public QuitarFavoritoUseCase(FavoritoRepository favoritoRepository) {
        this.favoritoRepository = favoritoRepository;
    }

    public void ejecutar(String usuarioId, String repuestoId) {
        if (!favoritoRepository.existe(usuarioId, repuestoId)) {
            throw new RecursoNoEncontradoException(
                "Ese repuesto no esta en tus favoritos."
            );
        }

        favoritoRepository.quitar(usuarioId, repuestoId);
    }
}
