package com.uniquindio.CompraVenta.domain.exception;

public class RecursoYaExisteException extends RuntimeException {

    public RecursoYaExisteException(String mensaje) {
        super(mensaje);
    }
}
