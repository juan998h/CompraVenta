package com.uniquindio.CompraVenta.infrastructure.config;

import com.uniquindio.CompraVenta.application.usecase.BuscarRepuestosCompatiblesUseCase;
import com.uniquindio.CompraVenta.application.usecase.ConfirmarPagoUseCase;
import com.uniquindio.CompraVenta.application.usecase.ConsultarHistorialDeComprasUseCase;
import com.uniquindio.CompraVenta.application.usecase.ConsultarRepuestosMasVendidosUseCase;
import com.uniquindio.CompraVenta.application.usecase.EliminarRepuestoUseCase;
import com.uniquindio.CompraVenta.application.usecase.ListarRepuestosUseCase;
import com.uniquindio.CompraVenta.application.usecase.PublicarRepuestoUseCase;
import com.uniquindio.CompraVenta.application.usecase.RealizarCompraUseCase;
import com.uniquindio.CompraVenta.application.usecase.RegistrarDescargaUseCase;
import com.uniquindio.CompraVenta.application.usecase.SolicitarReembolsoUseCase;
import com.uniquindio.CompraVenta.application.usecase.VerificarEspecialidadVendedorUseCase;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import com.uniquindio.CompraVenta.domain.repository.RepuestoRepository;
import com.uniquindio.CompraVenta.domain.repository.VendedorRepository;
import com.uniquindio.CompraVenta.domain.service.ServicioDeCompra;
import com.uniquindio.CompraVenta.domain.service.ServicioDeEliminacionDeRepuesto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Aqui se genera la aplicacion: los casos de uso y los servicios del dominio son clases
 * Java normales y esta configuracion los entrega ya conectados
 * a sus repositorios.
 */
@Configuration
public class CasosDeUsoConfig {

    // ---------- Servicios de dominio ----------

    @Bean
    public ServicioDeCompra servicioDeCompra(CompraRepository compraRepository) {
        return new ServicioDeCompra(compraRepository);
    }

    @Bean
    public ServicioDeEliminacionDeRepuesto servicioDeEliminacionDeRepuesto(
            CompraRepository compraRepository) {
        return new ServicioDeEliminacionDeRepuesto(compraRepository);
    }

    // ---------- Casos de uso del vendedor ----------

    @Bean
    public PublicarRepuestoUseCase publicarRepuestoUseCase(RepuestoRepository repuestoRepository,
                                                           VendedorRepository vendedorRepository) {
        return new PublicarRepuestoUseCase(repuestoRepository, vendedorRepository);
    }

    @Bean
    public EliminarRepuestoUseCase eliminarRepuestoUseCase(RepuestoRepository repuestoRepository,
                                                           ServicioDeEliminacionDeRepuesto servicio) {
        return new EliminarRepuestoUseCase(repuestoRepository, servicio);
    }

    @Bean
    public VerificarEspecialidadVendedorUseCase verificarEspecialidadVendedorUseCase(
            VendedorRepository vendedorRepository) {
        return new VerificarEspecialidadVendedorUseCase(vendedorRepository);
    }

    // ---------- Casos de uso del comprador ----------

    @Bean
    public RealizarCompraUseCase realizarCompraUseCase(RepuestoRepository repuestoRepository,
                                                       CompraRepository compraRepository,
                                                       ServicioDeCompra servicioDeCompra) {
        return new RealizarCompraUseCase(repuestoRepository, compraRepository, servicioDeCompra);
    }

    @Bean
    public ConfirmarPagoUseCase confirmarPagoUseCase(CompraRepository compraRepository) {
        return new ConfirmarPagoUseCase(compraRepository);
    }

    @Bean
    public RegistrarDescargaUseCase registrarDescargaUseCase(CompraRepository compraRepository) {
        return new RegistrarDescargaUseCase(compraRepository);
    }

    @Bean
    public SolicitarReembolsoUseCase solicitarReembolsoUseCase(CompraRepository compraRepository) {
        return new SolicitarReembolsoUseCase(compraRepository);
    }

    @Bean
    public ConsultarHistorialDeComprasUseCase consultarHistorialDeComprasUseCase(
            CompraRepository compraRepository) {
        return new ConsultarHistorialDeComprasUseCase(compraRepository);
    }

    // ---------- Consultas ----------

    @Bean
    public ListarRepuestosUseCase listarRepuestosUseCase(RepuestoRepository repuestoRepository) {
        return new ListarRepuestosUseCase(repuestoRepository);
    }

    @Bean
    public BuscarRepuestosCompatiblesUseCase buscarRepuestosCompatiblesUseCase(
            RepuestoRepository repuestoRepository) {
        return new BuscarRepuestosCompatiblesUseCase(repuestoRepository);
    }

    @Bean
    public ConsultarRepuestosMasVendidosUseCase consultarRepuestosMasVendidosUseCase(
            CompraRepository compraRepository) {
        return new ConsultarRepuestosMasVendidosUseCase(compraRepository);
    }
}
