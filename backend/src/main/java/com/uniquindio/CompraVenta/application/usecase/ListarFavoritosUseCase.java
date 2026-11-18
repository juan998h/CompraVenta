package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.repository.FavoritoRepository;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;

import java.util.List;
import java.util.Optional;

public class ListarFavoritosUseCase {

    private final FavoritoRepository favoritoRepository;
    private final RepuestoRepository repuestoRepository;

    public ListarFavoritosUseCase(
            FavoritoRepository favoritoRepository,
            RepuestoRepository repuestoRepository) {
        this.favoritoRepository = favoritoRepository;
        this.repuestoRepository = repuestoRepository;
    }

    public List<Repuesto> ejecutar(String usuarioId) {
        return favoritoRepository.idsDeRepuestosFavoritos(usuarioId)
            .stream()
            .map(repuestoRepository::obtenerPorId)
            .flatMap(Optional::stream)
            .filter(repuesto -> !repuesto.isEliminadoLogicamente())
            .toList();
    }
}
