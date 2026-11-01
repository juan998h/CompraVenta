package com.uniquindio.CompraVenta.infrastructure.security;

import com.uniquindio.CompraVenta.domain.entity.VendedorEspecializado;
import com.uniquindio.CompraVenta.domain.exception.ReglaDominioException;
import com.uniquindio.CompraVenta.domain.repository.VendedorRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Registro e inicio de sesion. Las contrasenas se guardan con BCrypt:
 * nunca se guarda ni se devuelve la contrasena real. En el login, el AuthenticationManager
 * busca al usuario por su correo y compara la contrasena con el hash.
 */
@Service
public class AutenticacionService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioJpaRepository usuarios;
    private final VendedorRepository vendedores;
    private final PasswordEncoder passwordEncoder;
    private final long minutosDeVida;

    public AutenticacionService(AuthenticationManager authenticationManager,
                                JwtTokenProvider jwtTokenProvider,
                                UsuarioJpaRepository usuarios,
                                VendedorRepository vendedores,
                                PasswordEncoder passwordEncoder,
                                @Value("${seguridad.jwt.duracion-minutos}") long minutosDeVida) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.usuarios = usuarios;
        this.vendedores = vendedores;
        this.passwordEncoder = passwordEncoder;
        this.minutosDeVida = minutosDeVida;
    }

    public TokenResponse login(LoginRequest request) {
        Authentication autenticacion = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        CustomUserDetails detalles = (CustomUserDetails) autenticacion.getPrincipal();
        List<String> roles = List.of(detalles.usuario().getRol().name());

        Instant ahora = Instant.now();
        Instant expira = ahora.plus(minutosDeVida, ChronoUnit.MINUTES);

        String token = jwtTokenProvider.generar(
                detalles.usuario().getId(), detalles.usuario().getCorreo(), roles, ahora, expira);

        return new TokenResponse(token, "Bearer", expira, roles);
    }

    @Transactional
    public UsuarioResponse registrar(RegistroRequest solicitud) {
        if (solicitud.rol() == RolUsuario.ADMIN) {
            throw new ReglaDominioException("No se puede registrar un administrador.");
        }
        if (usuarios.existsByCorreo(solicitud.correo())) {
            throw new ReglaDominioException("Ya existe un usuario con ese correo.");
        }
        String id = UUID.randomUUID().toString();
        if (solicitud.rol() == RolUsuario.VENDEDOR) {
            if (solicitud.especialidad() == null) {
                throw new ReglaDominioException("El vendedor debe declarar una especialidad.");
            }
            // El vendedor del negocio comparte el id del usuario: asi el token identifica a los dos.
            vendedores.guardar(VendedorEspecializado.registrar(id, solicitud.nombre(), solicitud.especialidad()));
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(id);
        usuario.setCorreo(solicitud.correo());
        usuario.setNombre(solicitud.nombre());
        usuario.setContrasenaHash(passwordEncoder.encode(solicitud.contrasena()));
        usuario.setRol(solicitud.rol());
        usuarios.save(usuario);

        return new UsuarioResponse(id, usuario.getCorreo(), usuario.getNombre(), usuario.getRol().name());
    }
}
