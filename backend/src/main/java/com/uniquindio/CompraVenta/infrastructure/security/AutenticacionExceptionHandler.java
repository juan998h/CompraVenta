package com.uniquindio.CompraVenta.infrastructure.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class AutenticacionExceptionHandler {

    // Correo inexistente o contrasena incorrecta: el mismo mensaje, para no decirle al atacante cual fallo
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> credencialesInvalidas(AuthenticationException ex) {
        Map<String, Object> cuerpo = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.UNAUTHORIZED.value(),
                "mensaje", "Usuario o contrasena incorrectos");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(cuerpo);
    }
}
