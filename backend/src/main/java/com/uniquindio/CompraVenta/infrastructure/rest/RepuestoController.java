package com.uniquindio.CompraVenta.infrastructure.rest;

import com.uniquindio.CompraVenta.application.dto.request.PublicarRepuestoRequest;
import com.uniquindio.CompraVenta.application.dto.response.PaginaResponse;
import com.uniquindio.CompraVenta.application.dto.response.RepuestoResponse;
import com.uniquindio.CompraVenta.application.usecase.BuscarRepuestosCompatiblesUseCase;
import com.uniquindio.CompraVenta.application.usecase.EliminarRepuestoUseCase;
import com.uniquindio.CompraVenta.application.usecase.ListarRepuestosUseCase;
import com.uniquindio.CompraVenta.application.usecase.PublicarRepuestoUseCase;
import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;
import com.uniquindio.CompraVenta.infrastructure.rest.mapper.RepuestoRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * Controller: recibe, delega en el caso de uso y responde.
 * No tiene reglas de negocio; el vendedor sale del token, no del cuerpo.
 */
@RestController
@RequestMapping("/api/repuestos")
@Tag(name = "Repuestos", description = "Publicar, listar, buscar y eliminar repuestos")
public class RepuestoController {

    private final PublicarRepuestoUseCase publicarRepuesto;
    private final ListarRepuestosUseCase listarRepuestos;
    private final BuscarRepuestosCompatiblesUseCase buscarCompatibles;
    private final EliminarRepuestoUseCase eliminarRepuesto;
    private final RepuestoRestMapper mapper;

    public RepuestoController(PublicarRepuestoUseCase publicarRepuesto,
        ListarRepuestosUseCase listarRepuestos,
        BuscarRepuestosCompatiblesUseCase buscarCompatibles,
        EliminarRepuestoUseCase eliminarRepuesto,
        RepuestoRestMapper mapper) {
        this.publicarRepuesto = publicarRepuesto;
        this.listarRepuestos = listarRepuestos;
        this.buscarCompatibles = buscarCompatibles;
        this.eliminarRepuesto = eliminarRepuesto;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Publica un repuesto",
            description = "Solo el rol VENDEDOR. El vendedor es quien tiene la sesion iniciada.")
    public ResponseEntity<RepuestoResponse> publicar(@Valid @RequestBody PublicarRepuestoRequest request,
                                                     Principal principal) {
        Repuesto repuesto = publicarRepuesto.ejecutar(
                UUID.randomUUID().toString(),
                principal.getName(),
                request.nombre(),
                mapper.aPrecio(request.monto(), request.moneda()),
                mapper.aCompatibilidad(request.modelosCompatibles()),
                mapper.aGarantia(request.mesesGarantia(), request.condicionesFabricante()),
                request.esCategoriaAltoRiesgo());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(repuesto.getId())
                .toUri();
        return ResponseEntity.created(location).body(mapper.aRepuestoResponse(repuesto));
    }

    @GetMapping
    @Operation(summary = "Lista los repuestos disponibles, paginados",
            description = "Busca por texto en el nombre y filtra por precio maximo. No muestra los eliminados.")
    public PaginaResponse<RepuestoResponse> listar(
            @RequestParam(name = "texto", required = false) String texto,
            @RequestParam(name = "precioMaximo", required = false) BigDecimal precioMaximo,
            @RequestParam(name = "pagina", defaultValue = "0") int pagina,
            @RequestParam(name = "tamano", defaultValue = "10") int tamano) {
        return mapper.aPaginaResponse(listarRepuestos.ejecutar(texto, precioMaximo, pagina, tamano));
    }

    @GetMapping("/compatibles")
    @Operation(summary = "Repuestos disponibles compatibles con un modelo de moto")
    public List<RepuestoResponse> compatibles(
            @RequestParam(name = "marca") String marca,
            @RequestParam(name = "version") String version,
            @RequestParam(name = "anio") int anio,
            @RequestParam(name = "cilindrajeCc") int cilindrajeCc) {
        Modelo modelo = mapper.aModelo(marca, version, anio, cilindrajeCc);
        return buscarCompatibles.ejecutar(modelo).stream()
                .map(mapper::aRepuestoResponse)
                .toList();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina (logicamente) un repuesto propio",
            description = "Solo el rol VENDEDOR dueño del repuesto, y solo si no tiene compras activas.")
    public ResponseEntity<Void> eliminar(@PathVariable("id") String id, Principal principal) {
        eliminarRepuesto.ejecutar(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
