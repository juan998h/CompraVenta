package com.uniquindio.CompraVenta.infrastructure.rest.mapper;

import com.uniquindio.CompraVenta.application.dto.request.ModeloRequest;
import com.uniquindio.CompraVenta.application.dto.response.ModeloResponse;
import com.uniquindio.CompraVenta.application.dto.response.PaginaResponse;
import com.uniquindio.CompraVenta.application.dto.response.RepuestoMasVendidoResponse;
import com.uniquindio.CompraVenta.application.dto.response.RepuestoResponse;
import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.repository.Pagina;
import com.uniquindio.CompraVenta.domain.repository.RepuestoMasVendido;
import com.uniquindio.CompraVenta.domain.valueobject.Cilindraje;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Traduce entre los DTO de la API y los objetos del dominio.
 * El controller solo recibe y responde; esta clase hace la conversion en los dos sentidos.
 */
@Component
public class RepuestoRestMapper {

    // ---------- De DTO a dominio ----------

    public Precio aPrecio(BigDecimal monto, String moneda) {
        if (moneda == null || moneda.isBlank()) {
            return Precio.enPesos(monto);
        }
        return new Precio(monto, moneda);
    }

    public Garantia aGarantia(int mesesGarantia, String condicionesFabricante) {
        return new Garantia(mesesGarantia, condicionesFabricante);
    }

    public Modelo aModelo(String marca, String version, int anio, int cilindrajeCc) {
        return new Modelo(marca, version, anio, new Cilindraje(cilindrajeCc));
    }

    public Modelo aModelo(ModeloRequest modelo) {
        return aModelo(modelo.marca(), modelo.version(), modelo.anio(), modelo.cilindrajeCc());
    }

    public Compatibilidad aCompatibilidad(List<ModeloRequest> modelos) {
        return new Compatibilidad(modelos.stream().map(this::aModelo).toList());
    }

    // ---------- De dominio a DTO ----------

    public RepuestoResponse aRepuestoResponse(Repuesto repuesto) {
        List<ModeloResponse> modelos = repuesto.getCompatibilidad().modelosCompatibles().stream()
                .map(m -> new ModeloResponse(m.marca(), m.version(), m.anio(),
                        m.cilindraje().centimetrosCubicos()))
                .toList();
        return new RepuestoResponse(
                repuesto.getId(),
                repuesto.getVendedorId(),
                repuesto.getNombre(),
                repuesto.getPrecio().monto(),
                repuesto.getPrecio().moneda(),
                repuesto.obtenerGarantia().mesesCobertura(),
                modelos);
    }

    public PaginaResponse<RepuestoResponse> aPaginaResponse(Pagina<Repuesto> pagina) {
        List<RepuestoResponse> contenido = pagina.contenido().stream()
                .map(this::aRepuestoResponse)
                .toList();
        return new PaginaResponse<>(contenido, pagina.totalElementos(), pagina.totalPaginas(),
                pagina.pagina(), pagina.tamano());
    }

    public RepuestoMasVendidoResponse aRankingResponse(RepuestoMasVendido fila) {
        return new RepuestoMasVendidoResponse(fila.repuestoId(), fila.nombre(), fila.unidades());
    }
}
