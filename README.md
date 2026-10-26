## Pruebas avanzadas

Todas las pruebas se ejecutan con ./gradlew test y siguen tres pasos: preparar, ejecutar y verificar. Además de las pruebas del dominio y de los casos de uso hicimos pruebas del repositorio y del controller con errores 404 y 400.

### 1. Seguridad de un endpoint (401, 403 y 200)
Probamos la ruta que confirma el pago de una compra (PUT /api/compras/{id}/confirmar), que solo puede usar el rol ADMIN. Sin token responde 401 porque el sistema no sabe quién es, con el rol COMPRADOR responde 403 porque lo reconoce pero no tiene permiso, y con el rol ADMIN responde 200.
Para no iniciar sesión de verdad usamos jwt() de spring-security-test el cual simula usuarios con diferentes roles y usamos @SpringBootTest para que se cargue la seguridad real.
también desactivamos CSRF porque nuestra API trabaja con tokens y no con sesiones de navegador entonces si estaba activo los POST y PUT daban 403 incluso con el rol correcto.

### 2. Diferencia entre @DataJpaTest y @SpringBootTest
@DataJpaTest prueba solo la parte que guarda y consulta los datos, con una base de datos H2 que revierte los cambios al terminar cada prueba por eso es más rápida.

@SpringBootTest carga casi toda la aplicación, así que hace pruebas más completas pero más lentas. 

Con @DataJpaTest tuvimos que usar @Import para agregar CompraRepositoryJpa y el mapper porque no los cargaba solo y Spring mostraba un error por no encontrarlos. En Spring Boot 4 la anotación cambió de paquete y hubo que agregar la dependencia spring-boot-starter-data-jpa-test. Al principio también nos salió un ClassNotFoundException que se resolvió agregando el driver de H2 en el build.gradle.

### 3. Prueba de integración
Compramos un repuesto y confirmamos el pago pasando por el controller, el caso de uso, el repositorio y la base de datos sin usar Mockito. Al final revisamos que la compra quedara completada y conservara el precio que tenía el repuesto al comprar. La diferencia con una prueba unitaria es que la unitaria se enfoca en una sola parte y simula lo demás, mientras que la de integración comprueba que todas las partes funcionen juntas. Como tarda más, la usamos solo para el proceso principal y dejamos los casos específicos a las pruebas unitarias.

Por ahora los repuestos se guardan en memoria y solo las compras usan JPA. como mejora el comprador debería salir del token y no del cuerpo de la petición.