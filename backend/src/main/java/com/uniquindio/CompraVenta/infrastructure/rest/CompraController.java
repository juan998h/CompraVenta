package com.uniquindio.CompraVenta.infrastructure.rest;

import com.uniquindio.CompraVenta.application.dto.request.RealizarCompraRequest;
import com.uniquindio.CompraVenta.application.dto.request.SolicitarReembolsoRequest;
import com.uniquindio.CompraVenta.application.dto.response.CompraResponse;
import com.uniquindio.CompraVenta.application.usecase.ConfirmarPagoUseCase;
import com.uniquindio.CompraVenta.application.usecase.ConsultarHistorialDeComprasUseCase;
import com.uniquindio.CompraVenta.application.usecase.RealizarCompraUseCase;
import com.uniquindio.CompraVenta.application.usecase.RegistrarDescargaUseCase;
import com.uniquindio.CompraVenta.application.usecase.SolicitarReembolsoUseCase;
import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.infrastructure.rest.mapper.CompraRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Controller: El comprador sale del token: nadie puede comprar a nombre de otro
 * escribiendolo en el cuerpo de la peticion.
 */
@RestController
@RequestMapping("/api/compras")
@Tag(name = "Compras", description = "Comprar, confirmar el pago, descargar, reembolsar y ver el historial")
public class CompraController {

    private final RealizarCompraUseCase realizarCompra;
    private final ConfirmarPagoUseCase confirmarPago;
    private final RegistrarDescargaUseCase registrarDescarga;
    private final SolicitarReembolsoUseCase solicitarReembolso;
    private final ConsultarHistorialDeComprasUseCase consultarHistorial;
    private final CompraRestMapper mapper;

    public CompraController(RealizarCompraUseCase realizarCompra,
                            ConfirmarPagoUseCase confirmarPago,
                            RegistrarDescargaUseCase registrarDescarga,
                            SolicitarReembolsoUseCase solicitarReembolso,
                            ConsultarHistorialDeComprasUseCase consultarHistorial,
                            CompraRestMapper mapper) {
        this.realizarCompra = realizarCompra;
        this.confirmarPago = confirmarPago;
        this.registrarDescarga = registrarDescarga;
        this.solicitarReembolso = solicitarReembolso;
        this.consultarHistorial = consultarHistorial;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Compra un repuesto (queda PENDIENTE)", description = "Solo el rol COMPRADOR.")
    public ResponseEntity<CompraResponse> crear(@Valid @RequestBody RealizarCompraRequest request,
                                                Principal principal) {
        Compra compra = realizarCompra.ejecutar(UUID.randomUUID().toString(),
                principal.getName(), request.repuestoId(), LocalDateTime.now());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(compra.getId())
                .toUri();
        return ResponseEntity.created(location).body(mapper.aResponse(compra));
    }

    @GetMapping("/mias")
    @Operation(summary = "Historial de mis compras, de la mas reciente a la mas antigua")
    public List<CompraResponse> misCompras(Principal principal) {
        return consultarHistorial.ejecutar(principal.getName()).stream()
                .map(mapper::aResponse)
                .toList();
    }

    @PutMapping("/{id}/confirmar")
    @Operation(summary = "Confirma el pago simulado (la compra pasa a COMPLETADA)")
    public CompraResponse confirmar(@PathVariable("id") String id) {
        return mapper.aResponse(confirmarPago.ejecutar(id));
    }

    @PutMapping("/{id}/descarga")
    @Operation(summary = "Registra la descarga del archivo (bloquea el reembolso)")
    public CompraResponse descargar(@PathVariable("id") String id) {
        return mapper.aResponse(registrarDescarga.ejecutar(id, LocalDateTime.now()));
    }

    @PutMapping("/{id}/reembolso")
    @Operation(summary = "Solicita el reembolso de una compra",
            description = "Reglas: compra COMPLETADA, dentro de 5 dias, sin descarga y con motivo.")
    public CompraResponse reembolsar(@PathVariable("id") String id,
                                     @Valid @RequestBody SolicitarReembolsoRequest request) {
        return mapper.aResponse(solicitarReembolso.ejecutar(id, request.motivo(), LocalDateTime.now()));
    }
}
