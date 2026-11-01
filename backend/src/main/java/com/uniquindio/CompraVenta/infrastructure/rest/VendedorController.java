package com.uniquindio.CompraVenta.infrastructure.rest;

import com.uniquindio.CompraVenta.application.dto.response.VendedorResponse;
import com.uniquindio.CompraVenta.application.usecase.VerificarEspecialidadVendedorUseCase;
import com.uniquindio.CompraVenta.infrastructure.rest.mapper.VendedorRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vendedores")
@Tag(name = "Vendedores", description = "Verificacion de la especialidad de los vendedores")
public class VendedorController {

    private final VerificarEspecialidadVendedorUseCase verificarEspecialidad;
    private final VendedorRestMapper mapper;

    public VendedorController(VerificarEspecialidadVendedorUseCase verificarEspecialidad,
                              VendedorRestMapper mapper) {
        this.verificarEspecialidad = verificarEspecialidad;
        this.mapper = mapper;
    }

    @PutMapping("/{id}/verificar")
    @Operation(summary = "Verifica la especialidad de un vendedor",
            description = "Solo el rol ADMIN (la plataforma). Un vendedor verificado puede publicar en categorias de alto riesgo.")
    public VendedorResponse verificar(@PathVariable("id") String id) {
        return mapper.aResponse(verificarEspecialidad.ejecutar(id));
    }
}
