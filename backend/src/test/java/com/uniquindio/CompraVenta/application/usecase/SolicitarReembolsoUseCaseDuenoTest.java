package com.uniquindio.CompraVenta.application.usecase;

import com.uniquindio.CompraVenta.domain.entity.Compra;
import com.uniquindio.CompraVenta.domain.exception.AccesoDenegadoException;
import com.uniquindio.CompraVenta.domain.repository.CompraRepository;
import com.uniquindio.CompraVenta.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SolicitarReembolsoUseCaseDuenoTest {

    @Test
    void solicitarReembolsoDeUnaCompraAjenaLanzaAccesoDenegadoYNoGuarda() {
        CompraRepository repository = mock(CompraRepository.class);
        Compra compra = Compra.realizar("c-1", "rep-1", "ana",
                Precio.enPesos(new BigDecimal("15000")),
                LocalDateTime.of(2026, 10, 1, 10, 0));
        compra.completar();
        when(repository.obtenerPorId("c-1")).thenReturn(Optional.of(compra));
        SolicitarReembolsoUseCase useCase =
                new SolicitarReembolsoUseCase(repository);

        assertThrows(AccesoDenegadoException.class,
                () -> useCase.ejecutar("c-1", "otro-comprador", "No me sirvio",
                        LocalDateTime.of(2026, 10, 2, 9, 0)));
        verify(repository, never()).guardar(any(Compra.class));
    }
}
