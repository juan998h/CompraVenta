package com.uniquindio.CompraVenta.application.dto.response;

import java.util.List;

/** El listado paginado devuelve el contenido, los totales y la pagina pedida. */
public record PaginaResponse<T>(List<T> contenido, long totalElementos, int totalPaginas,
                                int pagina, int tamano) {
}
