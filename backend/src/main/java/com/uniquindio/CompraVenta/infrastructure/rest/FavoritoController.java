package com.uniquindio.CompraVenta.infrastructure.rest;

import com.uniquindio.CompraVenta.application.dto.response.RepuestoResponse;
import com.uniquindio.CompraVenta.application.usecase.AgregarFavoritoUseCase;
import com.uniquindio.CompraVenta.application.usecase.ListarFavoritosUseCase;
import com.uniquindio.CompraVenta.application.usecase.QuitarFavoritoUseCase;
import com.uniquindio.CompraVenta.infrastructure.rest.mapper.RepuestoRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
@Tag(name = "Favoritos", description = "Repuestos favoritos del comprador")
public class FavoritoController {

    private final AgregarFavoritoUseCase agregarFavorito;
    private final QuitarFavoritoUseCase quitarFavorito;
    private final ListarFavoritosUseCase listarFavoritos;
    private final RepuestoRestMapper mapper;

    public FavoritoController(
            AgregarFavoritoUseCase agregarFavorito,
            QuitarFavoritoUseCase quitarFavorito,
            ListarFavoritosUseCase listarFavoritos,
            RepuestoRestMapper mapper) {
        this.agregarFavorito = agregarFavorito;
        this.quitarFavorito = quitarFavorito;
        this.listarFavoritos = listarFavoritos;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "Lista mis repuestos favoritos")
    public List<RepuestoResponse> misFavoritos(Principal principal) {
        return listarFavoritos.ejecutar(principal.getName())
            .stream()
            .map(mapper::aRepuestoResponse)
            .toList();
    }

    @PostMapping("/{repuestoId}")
    @Operation(summary = "Marca un repuesto como favorito")
    public ResponseEntity<Void> agregar(
            @PathVariable("repuestoId") String repuestoId,
            Principal principal) {
        agregarFavorito.ejecutar(principal.getName(), repuestoId);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{repuestoId}")
    @Operation(summary = "Quita un repuesto de mis favoritos")
    public ResponseEntity<Void> quitar(
            @PathVariable("repuestoId") String repuestoId,
            Principal principal) {
        quitarFavorito.ejecutar(principal.getName(), repuestoId);
        return ResponseEntity.noContent().build();
    }
}
