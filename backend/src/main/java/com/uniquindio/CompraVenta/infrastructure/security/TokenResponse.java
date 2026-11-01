package com.uniquindio.CompraVenta.infrastructure.security;

import java.time.Instant;
import java.util.List;

public record TokenResponse(String token, String tipo, Instant expira, List<String> roles) {
}
