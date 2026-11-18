package com.uniquindio.CompraVenta.domain.repository;

import java.util.List;

public interface FavoritoRepository {
    boolean existe(String usuarioId, String repuestoId);
    void agregar(String usuarioId, String repuestoId);
    void quitar(String usuarioId, String repuestoId);
    List<String> idsDeRepuestosFavoritos(String usuarioId);
}
