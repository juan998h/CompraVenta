package com.uniquindio.CompraVenta.domain.repository;

import java.util.List;

/**
 * Una pagina de resultados. Es del dominio a proposito: el dominio no importa
 * la paginacion de Spring; el adaptador de infraestructura hace la conversion.
 */
public record Pagina<T>(List<T> contenido, long totalElementos, int totalPaginas,
                        int pagina, int tamano) {
}
