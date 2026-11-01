package com.uniquindio.CompraVenta.domain.repository;

/** Una fila del ranking de ventas: cuantas compras completadas tiene un repuesto. */
public record RepuestoMasVendido(String repuestoId, String nombre, Long unidades) {}
