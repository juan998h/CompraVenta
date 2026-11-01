package com.uniquindio.CompraVenta.infrastructure.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacion", description = "Registro e inicio de sesion (rutas publicas)")
public class AuthController {

    private final AutenticacionService autenticacion;

    public AuthController(AutenticacionService autenticacion) {
        this.autenticacion = autenticacion;
    }

    @PostMapping("/registro")
    @Operation(summary = "Registra un comprador o un vendedor",
            description = "Un vendedor debe indicar su especialidad. El rol ADMIN no se puede registrar.")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest solicitud) {
        return ResponseEntity.status(201).body(autenticacion.registrar(solicitud));
    }

    @PostMapping("/login")
    @Operation(summary = "Inicia sesion y devuelve el token JWT",
            description = "Use el token en el boton Authorize de Swagger o en el encabezado Authorization: Bearer <token>.")
    public TokenResponse iniciarSesion(@Valid @RequestBody LoginRequest solicitud) {
        return autenticacion.login(solicitud);
    }
}
