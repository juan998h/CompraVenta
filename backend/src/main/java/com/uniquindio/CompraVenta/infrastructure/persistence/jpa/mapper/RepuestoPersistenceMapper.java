package com.uniquindio.CompraVenta.infrastructure.persistence.jpa.mapper;

import com.uniquindio.CompraVenta.domain.entity.Repuesto;
import com.uniquindio.CompraVenta.domain.valueobject.Cilindraje;
import com.uniquindio.CompraVenta.domain.valueobject.Compatibilidad;
import com.uniquindio.CompraVenta.domain.valueobject.Garantia;
import com.uniquindio.CompraVenta.domain.valueobject.Modelo;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import com.uniquindio.CompraVenta.infrastructure.persistence.jpa.entity.GarantiaEmbeddable;
import com.uniquindio.CompraVenta.infrastructure.persistence.jpa.entity.ModeloEmbeddable;
import com.uniquindio.CompraVenta.infrastructure.persistence.jpa.entity.PrecioEmbeddable;
import com.uniquindio.CompraVenta.infrastructure.persistence.jpa.entity.RepuestoEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RepuestoPersistenceMapper {

    public RepuestoEntity toEntity(Repuesto repuesto) {
        RepuestoEntity entity = new RepuestoEntity();
        entity.setId(repuesto.getId());
        entity.setVendedorId(repuesto.getVendedorId());
        entity.setNombre(repuesto.getNombre());
        entity.setPrecio(new PrecioEmbeddable(
                repuesto.getPrecio().monto(),
                repuesto.getPrecio().moneda()));
        Garantia garantia = repuesto.obtenerGarantia();
        entity.setGarantia(new GarantiaEmbeddable(
                garantia.mesesCobertura(),
                garantia.condicionesFabricante()));
        entity.setEliminado(repuesto.isEliminadoLogicamente());
        List<ModeloEmbeddable> modelos = new ArrayList<>();
        for (Modelo modelo : repuesto.getCompatibilidad().modelosCompatibles()) {
            modelos.add(new ModeloEmbeddable(
                    modelo.marca(),
                    modelo.version(),
                    modelo.anio(),
                    modelo.cilindraje().centimetrosCubicos()));
        }
        entity.setModelos(modelos);
        return entity;
    }

    public Repuesto toDomain(RepuestoEntity entity) {
        List<Modelo> modelos = entity.getModelos().stream()
                .map(m -> new Modelo(m.getMarca(), m.getVersionModelo(), m.getAnio(),
                        new Cilindraje(m.getCilindrajeCc())))
                .toList();
        return Repuesto.reconstruir(
                entity.getId(),
                entity.getVendedorId(),
                entity.getNombre(),
                new Precio(entity.getPrecio().getMonto(), entity.getPrecio().getMoneda()),
                new Compatibilidad(modelos),
                new Garantia(entity.getGarantia().getMesesCobertura(),
                        entity.getGarantia().getCondicionesFabricante()),
                entity.isEliminado());
    }
}
