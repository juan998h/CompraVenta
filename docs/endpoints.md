# Endpoints de la API

Documentacion interactiva: http://localhost:8080/swagger-ui.html.

Todo error responde un JSON con la misma forma: { "timestamp", "status", "mensaje" }.

| Codigo | Cuando |
|---|---|
| 400 | Datos mal enviados: campos vacios, parametro faltante, pagina negativa |
| 401 | No se envio token o el token no es valido |
| 403 | El token es valido pero el rol no tiene permiso |
| 404 | El recurso no existe |
| 422 | Se rompio una regla de negocio (la lanza el dominio) |

El comprador y el vendedor NUNCA viajan en el cuerpo de la peticion: salen del token.

| Metodo y ruta | Caso de uso | Rol | Respuesta |
|---|---|---|---|
| POST /api/repuestos | PublicarRepuesto | VENDEDOR | 201 + Location |
| GET /api/repuestos?texto=&precioMaximo=&pagina=&tamano= | ListarRepuestos (paginado) | cualquier usuario con sesion | 200 |
| GET /api/repuestos/compatibles?marca=&version=&anio=&cilindrajeCc= | BuscarRepuestosCompatibles | cualquier usuario con sesion | 200 |
| DELETE /api/repuestos/{id} | EliminarRepuesto | VENDEDOR | 204 |
| PUT /api/vendedores/{id}/verificar | VerificarEspecialidadVendedor | ADMIN | 200 |
| POST /api/compras | RealizarCompra | COMPRADOR | 201 + Location |
| PUT /api/compras/{id}/confirmar | ConfirmarPago | COMPRADOR | 200 |
| PUT /api/compras/{id}/descarga | RegistrarDescarga | COMPRADOR | 200 |
| PUT /api/compras/{id}/reembolso | SolicitarReembolso | COMPRADOR | 200 |
| GET /api/compras/mias | ConsultarHistorialDeCompras | COMPRADOR | 200 |
| GET /api/reportes/repuestos-mas-vendidos?limite= | ConsultarRepuestosMasVendidos | VENDEDOR o ADMIN | 200 |

Las rutas de registro e inicio de sesion (/api/auth) se documentan en el README.
