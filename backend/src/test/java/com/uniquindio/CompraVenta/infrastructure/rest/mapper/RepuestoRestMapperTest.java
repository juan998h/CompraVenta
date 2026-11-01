package com.uniquindio.CompraVenta.infrastructure.rest.mapper;

import com.uniquindio.CompraVenta.application.dto.request.ModeloRequest;
import com.uniquindio.CompraVenta.application.dto.response.PaginaResponse;
import com.uniquindio.CompraVenta.application.dto.response.RepuestoResponse;
import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.repository.Pagina;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RepuestoRestMapperTest {

    private final RepuestoRestMapper mapper = new RepuestoRestMapper();

    @Test
    void deberiaUsarPesosCuandoNoSeIndicaLaMoneda() {
        // Act
        Precio precio = mapper.aPrecio(new BigDecimal("150000"), null);

        // Assert
        assertEquals("COP", precio.moneda());
        assertEquals(new BigDecimal("150000.00"), precio.monto());
    }

    @Test
    void deberiaConvertirLosModelosDelRequestEnUnaCompatibilidad() {
        // Arrange
        List<ModeloRequest> modelos = List.of(
                new ModeloRequest("Yamaha", "MT-07", 2022, 689),
                new ModeloRequest("Kawasaki", "Z900", 2023, 948));

        // Act
        Compatibilidad compatibilidad = mapper.aCompatibilidad(modelos);

        // Assert
        assertEquals(2, compatibilidad.modelosCompatibles().size());
        assertEquals(948, compatibilidad.modelosCompatibles().get(1).cilindraje().centimetrosCubicos());
    }

    @Test
    void deberiaConvertirUnRepuestoYUnaPaginaEnSusRespuestas() {
        // Arrange
        Repuesto repuesto = Repuesto.publicar("r-1", "vend-1", "Pastillas de freno",
                mapper.aPrecio(new BigDecimal("250000"), "COP"),
                mapper.aCompatibilidad(List.of(new ModeloRequest("Yamaha", "MT-07", 2022, 689))),
                mapper.aGarantia(12, "Garantia de fabrica"));

        // Act
        RepuestoResponse respuesta = mapper.aRepuestoResponse(repuesto);
        PaginaResponse<RepuestoResponse> pagina =
                mapper.aPaginaResponse(new Pagina<>(List.of(repuesto), 1, 1, 0, 10));

        // Assert
        assertEquals("r-1", respuesta.repuestoId());
        assertEquals(12, respuesta.mesesGarantia());
        assertEquals("MT-07", respuesta.modelosCompatibles().get(0).version());
        assertEquals(1, pagina.totalElementos());
        assertEquals("Pastillas de freno", pagina.contenido().get(0).nombre());
    }
}
