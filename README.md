# Bikers Dreams

Marketplace de repuestos para motocicletas de alto cilindraje (400 cc o más). Proyecto de Programación Avanzada, Universidad del Quindío. Spring Boot 4, Java 21 y  arquitectura hexagonal.

**Integrantes:** Juan Daniel Vera y Juan Esteban Guitierrez

## Cómo ejecutar

1. Entrar a la carpeta backend.
2. Ejecutar .\gradlew.bat bootRun (en Linux o Mac ./gradlew bootRun).
3. API en http://localhost:8080 y Swagger en http://localhost:8080/swagger-ui.html
4. Pruebas: ./gradlew test

La base de datos es H2 en memoria. Se crea y se llena sola con data.sql al arrancar.

## Usuarios de prueba

| Correo | Contraseña | Rol |
|---|---|---|
| anamaria1996@bikers.com | Comprador123* | COMPRADOR |
| motopartsSAS@bikers.com | Vendedor123* | VENDEDOR |
| admin2020@bikers.com | Admin123* | ADMIN |

En Swagger: POST /api/auth/login (username es el correo), copiar el token y pegarlo en el botón Authorize.

## Rutas y roles

| Ruta | Quién |
|---|---|
| POST /api/auth/registro y /api/auth/login | Públicas |
| POST y DELETE /api/repuestos | VENDEDOR |
| GET /api/repuestos y /api/repuestos/compatibles | Cualquier usuario con sesión |
| PUT /api/vendedores/{id}/verificar | ADMIN |
| PUT /api/compras/{id}/confirmar | ADMIN |
| POST /api/compras, GET /api/compras/mias, PUT descarga y reembolso | COMPRADOR |
| GET /api/reportes/repuestos-mas-vendidos | VENDEDOR o ADMIN |

Sin token responde 401. Con un rol sin permiso responde 403. El comprador y el vendedor salen del token, no del cuerpo de la petición.

## Seguridad

- Las contraseñas se guardan con BCrypt (hash de una sola vía). Deben tener 8 caracteres, una mayúscula y un número.
- El login devuelve un JWT firmado con HS256 que lleva el id, el correo y el rol, y vence a los 60 minutos.
- La clave secreta y la duración están en application.properties.
- Cualquiera puede leer un token, pero nadie puede cambiarlo sin invalidar la firma.

## Correcciones de la entrega anterior

| Observación de la docente | Qué hicimos | Dónde quedó |
|---|---|---|
| El constructor no puede tener lógica | Los constructores solo asignan campos. Las validaciones pasaron a los metodos  (publicar, realizar, registrar). Para cargar desde la base de datos se agregó reconstruir, que no valida | Compra, Repuesto y VendedorEspecializado (domain/entity) |
| Repuesto debe tener más lógica, porque es la raíz del agregado más grande | Repuesto ahora protege sus propias reglas con renombrar, cambiarPrecio, actualizarGarantia, agregarModeloCompatible, quitarModeloCompatible, venderUnidad, eliminarLogicamente, reactivar y verificarQueLoPuedeEliminar | Repuesto (domain/entity) y docs/invariantes.md |

## Pruebas

Todas siguen preparar, ejecutar y verificar, y se corren con ./gradlew test.

- **Seguridad de un endpoint:** confirmar un pago sin token da 401, con rol COMPRADOR da 403 y con ADMIN da 200. Se usa jwt() de spring-security-test y @SpringBootTest. CSRF está desactivado porque la API usa tokens y no sesiones.
- **@DataJpaTest vs @SpringBootTest:** @DataJpaTest solo carga la parte de datos con H2 y revierte los cambios, por eso es más rápido. @SpringBootTest carga casi toda la aplicación. En Spring Boot 4 la anotación cambió de paquete y hubo que agregar spring-boot-starter-data-jpa-test y @Import para los repositorios.
- **Integración:** se compra un repuesto y se confirma el pago pasando por controller, caso de uso, repositorio y base de datos, sin Mockito. Se revisa que la compra quede completada con el precio que tenía el repuesto.
- **Login y roles:** se hace login real y se usa el token. Un comprador no puede verificar vendedores (403), un vendedor no puede comprar (403), un token inventado da 401 y el admin sí puede verificar.

## Mejoras pendientes

- Conectar MariaDB en lugar de H2.
- Verificar que la compra que se confirma, descarga o reembolsa pertenezca al usuario con sesión.