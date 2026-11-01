package com.uniquindio.CompraVenta.infrastructure.config;

import com.uniquindio.CompraVenta.application.usecase.ConfirmarPagoUseCase;
import com.uniquindio.CompraVenta.application.usecase.RealizarCompraUseCase;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;
import com.uniquindio.CompraVenta.domain.service.ServicioDeCompra;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CasosDeUsoConfig {

    // Ya no hay bean de RepuestoRepository en memoria: ahora lo provee RepuestoRepositoryJpa.

    @Bean
    public ServicioDeCompra servicioDeCompra(CompraRepository compraRepository) {
        return new ServicioDeCompra(compraRepository);
    }

    @Bean
    public RealizarCompraUseCase realizarCompraUseCase(RepuestoRepository repuestoRepository,
     CompraRepository compraRepository, ServicioDeCompra servicioDeCompra) {
        return new RealizarCompraUseCase(repuestoRepository, compraRepository, servicioDeCompra);
    }

    @Bean
    public ConfirmarPagoUseCase confirmarPagoUseCase(CompraRepository compraRepository) {
        return new ConfirmarPagoUseCase(compraRepository);
    }
}
