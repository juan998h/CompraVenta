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
| El registro con correo repetido respondia 422 | Se lanza RecursoYaExisteException y se responde 409 | AutenticacionService y GlobalExceptionHandler |
| Otro comprador podia descargar o pedir reembolso de compras ajenas | Se verifica el comprador del token y se responde 403 si no es el dueno | Compra, casos de uso, CompraController y GlobalExceptionHandler |

## Pruebas avanzadas

Todas siguen preparar, ejecutar y verificar, y se corren con ./gradlew test.

### 1. Probar un endpoint con seguridad
Probamos la ruta que confirma un pago, que es solo para el administrador.
Hicimos tres pruebas sobre la misma ruta.
Sin token responde 401, porque el sistema no sabe quién es el usuario.
Con token de COMPRADOR responde 403, porque sabe quién es pero no tiene permiso.
Con token de ADMIN responde 200 y la compra se confirma.
Para no hacer login en cada prueba usamos jwt() de spring-security-test, que simula un usuario con un rol.
Usamos @SpringBootTest porque necesitamos la seguridad real cargada.
CSRF está desactivado porque la API usa tokens y no sesiones.

### 2. @DataJpaTest contra @SpringBootTest
@DataJpaTest solo carga la parte de datos: las entidades, los repositorios y una base H2 en memoria.
Al terminar cada prueba deshace los cambios, así que los datos no se mezclan entre pruebas.
Por eso es más rápido y es ideal para probar un repositorio.
@SpringBootTest carga casi toda la aplicación: controllers, casos de uso, seguridad y base de datos.
Es más lento, pero sirve cuando queremos probar varias partes juntas.
En Spring Boot 4 @DataJpaTest cambió de paquete y necesita un starter de pruebas aparte.
Lo resolvimos agregando spring-boot-starter-data-jpa-test en el build.gradle.
También usamos @Import para traer nuestros repositorios, porque esta anotación no los carga sola.

### 3. Una prueba de integración
Nuestra prueba compra un repuesto y confirma el pago.
Recorre el sistema real: controller, caso de uso, repositorio y base de datos, sin simular nada.
Al final revisamos que la compra quede completada con el precio que tenía el repuesto.
Una prueba unitaria revisa una sola clase aislada, y lo que no es suyo se simula con Mockito.
Es rápida y cuando falla sabemos exactamente dónde está el error.
La de integración comprueba que las piezas funcionan bien juntas, pero es más lenta y si falla cuesta más ubicar el error.
Conviene usar unitarias para las reglas del negocio y de integración para los flujos importantes.

## Mejoras pendientes

- Conectar MariaDB en lugar de H2.

## Frontend (Entrega 3)

<!-- frontend-entrega3-final -->

### Pantallas disponibles

| Pantalla | Acceso | Funcion |
|---|---|---|
| Login | Sin sesion | Autentica y guarda el JWT |
| Registro | Sin sesion | Crea compradores y vendedores; exige especialidad al vendedor |
| Catalogo | Con sesion | Busca y pagina repuestos desde el backend |
| Publicar | VENDEDOR | Publica repuestos para motos de 400 cc o mas |
| Eliminar repuesto propio | VENDEDOR dueno | Elimina publicaciones propias sin compras activas |
| Mis compras | COMPRADOR | Muestra historial, registra descarga y solicita reembolso |
| Favoritos | COMPRADOR | Agrega, consulta y quita favoritos guardados |
| Confirmar compras | ADMIN | Consulta pendientes y confirma el pago simulado |
| Reportes | VENDEDOR o ADMIN | Muestra el ranking con limites de 3, 5 o 10 |
| Buscar por moto | Con sesion | Busca por marca, modelo y ano; valida el cilindraje |
| Vendedores | ADMIN | Verifica vendedores para categorias de alto riesgo |

### Seguridad y reglas

- Los guards controlan la navegacion por sesion y rol.
- El interceptor agrega el JWT y atiende respuestas 401.
- El backend valida token, roles, propiedad y reglas del negocio.
- El reembolso exige compra COMPLETADA, dentro de cinco dias desde la compra, sin descarga y con motivo.
- La descarga registra una fecha y bloquea el reembolso; no entrega un archivo real.
- La confirmacion del pago y el reembolso son operaciones simuladas.
- Las operaciones muestran avisos y bloquean sus botones mientras se envian.
- H2 esta en memoria: recargar el navegador conserva los datos; reiniciar el backend los reinicia.

### Ejecutar el frontend

Desde la raiz del repositorio:

    cd frontend
    npm.cmd install
    npm.cmd start

Interfaz: http://localhost:4200
Backend requerido: http://localhost:8080

Compilacion de produccion desde la raiz:

    npm.cmd --prefix frontend run build



Aplicación Angular conectada al backend de Bikers Dreams. En la aplicación la marca se ve como "Sueños de motociclistas". Permite iniciar sesión con JWT, ver el catálogo de repuestos con búsqueda y paginación, publicar repuestos (vendedor), comprar y guardar favoritos (comprador). Usa Bootstrap 5 y es responsive.

### Cómo ejecutar el frontend

1. Tener el backend corriendo (ver "Cómo ejecutar" arriba) en http://localhost:8080.
2. Abrir otra terminal y entrar a la carpeta frontend.
3. Ejecutar npm install (solo la primera vez).
4. Ejecutar ng serve.
5. Abrir http://localhost:4200 en el navegador.

El backend acepta peticiones desde http://localhost:4200 (CORS configurado en SeguridadConfig).

### Usuarios de prueba

Son los mismos de la tabla de arriba. Para probar el frontend:

- Comprador: anamaria1996@bikers.com / Comprador123*
- Vendedor: motopartsSAS@bikers.com / Vendedor123*

### Pantallas

| Pantalla | Ruta | Quién entra |
|---|---|---|
| Login | /login | Solo quien no tiene sesión |
| Catálogo (búsqueda, paginación, comprar, favorito) | /catalogo | Cualquier usuario con sesión |
| Publicar repuesto | /publicar | VENDEDOR |
| Favoritos | /favoritos | COMPRADOR |

### Cómo está organizado

- services: acceso al backend (auth, repuestos, compras, favoritos), sesión y avisos.
- interceptors: agrega el token a cada petición y, si el backend responde 401, cierra la sesión y avisa.
- guards: autenticado, invitado y rol.
- components: login, navbar, catálogo, publicar, favoritos y avisos.
- La URL del backend está en environments (apiUrl).

### Funcionalidad propia: favoritos

El comprador marca repuestos como favoritos. Se guarda en el backend (/api/favoritos, solo COMPRADOR) y sigue ahí al recargar la página mientras el backend esté corriendo, porque la base de datos es H2 en memoria.

### División del trabajo

| Integrante | Usuario de GitHub | Qué hizo |
|---|---|---|
| Juan Daniel Vera | juandanielvera | Configuración del frontend, CORS, modelos y servicios, login con JWT, interceptor, navbar, avisos, diseño visual (fotos, portada y estilo personalizado) |
| Juan Esteban Guitierrez | juan998h | Guards, catálogo con paginación y búsqueda, publicar y comprar, favoritos (backend y frontend) |
