package com.uniquindio.CompraVenta.infrastructure.rest;

import com.uniquindio.CompraVenta.application.dto.request.RealizarCompraRequest;
import com.uniquindio.CompraVenta.application.dto.response.CompraResponse;
import com.uniquindio.CompraVenta.application.usecase.ConfirmarPagoUseCase;
import com.uniquindio.CompraVenta.application.usecase.RealizarCompraUseCase;
import com.uniquindio.CompraVenta.domain.entity.Compra;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    private final RealizarCompraUseCase realizarCompra;
    private final ConfirmarPagoUseCase confirmarPago;

    public CompraController(RealizarCompraUseCase realizarCompra,
                            ConfirmarPagoUseCase confirmarPago) {
        this.realizarCompra = realizarCompra;
        this.confirmarPago = confirmarPago;
    }

    @PostMapping
    public ResponseEntity<CompraResponse> crear(@Valid @RequestBody RealizarCompraRequest request) {
        Compra compra = realizarCompra.ejecutar(UUID.randomUUID().toString(),
                request.compradorId(), request.repuestoId(), LocalDateTime.now());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(compra.getId())
                .toUri();
        return ResponseEntity.created(location).body(CompraResponse.desde(compra));
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<CompraResponse> confirmar(@PathVariable String id) {
        return ResponseEntity.ok(CompraResponse.desde(confirmarPago.ejecutar(id)));
    }
}