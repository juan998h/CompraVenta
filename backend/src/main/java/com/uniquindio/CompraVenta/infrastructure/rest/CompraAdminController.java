package com.uniquindio.CompraVenta.infrastructure.rest;

import com.uniquindio.CompraVenta.application.dto.response.CompraResponse;
import com.uniquindio.CompraVenta.application.usecase.ListarComprasPendientesUseCase;
import com.uniquindio.CompraVenta.infrastructure.rest.mapper.CompraRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller del administrador: lista las compras que esperan confirmacion de pago.
 * Confirmar sigue siendo PUT /api/compras/{id}/confirmar.
 */
@RestController
@RequestMapping("/api/compras")
@Tag(name = "Compras", description = "Comprar, confirmar el pago, descargar, reembolsar y ver el historial")
public class CompraAdminController {

    private final ListarComprasPendientesUseCase listarPendientes;
    private final CompraRestMapper mapper;

    public CompraAdminController(ListarComprasPendientesUseCase listarPendientes,
                                 CompraRestMapper mapper) {
        this.listarPendientes = listarPendientes;
        this.mapper = mapper;
    }

    @GetMapping("/pendientes")
    @Operation(summary = "Compras PENDIENTES de confirmar, de la mas antigua a la mas reciente",
            description = "Solo el rol ADMIN.")
    public List<CompraResponse> pendientes() {
        return listarPendientes.ejecutar().stream()
                .map(mapper::aResponse)
                .toList();
    }
}