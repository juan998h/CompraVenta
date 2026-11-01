package com.uniquindio.CompraVenta.infrastructure.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsuariosIniciales implements CommandLineRunner {

    private final UsuarioJpaRepository usuarios;
    private final PasswordEncoder passwordEncoder;

    public UsuariosIniciales(UsuarioJpaRepository usuarios, PasswordEncoder passwordEncoder) {
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        crearSiNoExiste("ana", "anamaria1996@bikers.com", "Ana", "Comprador123*", RolUsuario.COMPRADOR);
        crearSiNoExiste("vendedor1", "motopartsSAS@bikers.com", "Moto Parts Colombia", "Vendedor123*", RolUsuario.VENDEDOR);
        crearSiNoExiste("admin", "admin2020@bikers.com", "Administrador", "Admin123*", RolUsuario.ADMIN);
    }

    private void crearSiNoExiste(String id, String correo, String nombre, String contrasena, RolUsuario rol) {
        if (usuarios.existsByCorreo(correo)) {
            return;
        }
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(id);
        usuario.setCorreo(correo);
        usuario.setNombre(nombre);
        usuario.setContrasenaHash(passwordEncoder.encode(contrasena));
        usuario.setRol(rol);
        usuarios.save(usuario);
    }
}
