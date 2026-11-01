package com.uniquindio.CompraVenta.infrastructure.rest;

import com.uniquindio.CompraVenta.application.dto.response.RepuestoMasVendidoResponse;
import com.uniquindio.CompraVenta.application.usecase.ConsultarRepuestosMasVendidosUseCase;
import com.uniquindio.CompraVenta.infrastructure.rest.mapper.RepuestoRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Rankings calculados por la base de datos")
public class ReporteController {

    private final ConsultarRepuestosMasVendidosUseCase repuestosMasVendidos;
    private final RepuestoRestMapper mapper;

    public ReporteController(ConsultarRepuestosMasVendidosUseCase repuestosMasVendidos,
                             RepuestoRestMapper mapper) {
        this.repuestosMasVendidos = repuestosMasVendidos;
        this.mapper = mapper;
    }

    @GetMapping("/repuestos-mas-vendidos")
    @Operation(summary = "Ranking de repuestos mas vendidos",
            description = "Cuenta las compras COMPLETADAS de cada repuesto (JOIN + GROUP BY + COUNT).")
    public List<RepuestoMasVendidoResponse> masVendidos(
            @RequestParam(name = "limite", defaultValue = "5") int limite) {
        return repuestosMasVendidos.ejecutar(limite).stream()
                .map(mapper::aRankingResponse)
                .toList();
    }
}
