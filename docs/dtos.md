# DTOs (Request y Response)

Un DTO lleva datos entre la API y el dominio.
No contiene reglas: las reglas las valida el dominio. El DTO de entrada solo revisa el formato (campo vacío, número negativo).

El comprador y el vendedor no son campos de ningún DTO de entrada: salen del token de quien hace la petición.

## 1. PublicarRepuestoRequest
Caso de uso: PublicarRepuesto. Operación del dominio: VendedorEspecializado.publicarRepuesto(), que crea el Repuesto.

| Campo | Tipo | Mapea a | Por qué es necesario |
|---|---|---|---|
| nombre | String | Repuesto.nombre | Todo repuesto debe tener nombre |
| monto | BigDecimal | Precio.monto | El precio debe ser mayor a cero |
| moneda | String (opcional, por defecto COP) | Precio.moneda | El precio lleva moneda |
| modelosCompatibles | Lista de (marca, versión, año, cilindrajeCc) | Compatibilidad, con sus Modelo y Cilindraje | Un repuesto debe declarar al menos un modelo compatible |
| mesesGarantia | int | Garantia.mesesCobertura | La garantía es obligatoria |
| condicionesFabricante | String | Garantia.condicionesFabricante | La garantía depende de las condiciones del fabricante |
| esCategoriaAltoRiesgo | boolean | Parámetro de publicarRepuesto | Decide si se exige que el vendedor esté verificado |

El vendedor que publica sale del token, y con ese id se carga el VendedorEspecializado con VendedorRepository para aplicar la regla de verificación.


## 2. RealizarCompraRequest
Caso de uso: RealizarCompra. Operación del dominio: ServicioDeCompra.realizarCompra(), que crea la Compra.

| Campo | Tipo | Mapea a | Por qué es necesario |
|---|---|---|---|
| repuestoId | String | Se carga el Repuesto y se guarda en Compra.repuestoId | Saber qué se compra |

El comprador sale del token y se guarda en Compra.compradorId.


## 3. CompraResponse
Se arma a partir de la entidad Compra.

| Campo | Tipo | Viene de | Por qué es necesario |
|---|---|---|---|
| compraId | String | Compra.id | Identificar la compra en solicitudes posteriores |
| repuestoId | String | Compra.repuestoId | Saber qué se compró |
| compradorId | String | Compra.compradorId | Saber quién compró |
| monto y moneda | BigDecimal y String | Compra.precioPagado | Mostrar el precio congelado al momento de comprar |
| estado | String | Compra.estado | Saber si está pendiente, completada o reembolsada |
| fechaCompra | fecha y hora | Compra.fechaCompra | Mostrar cuándo se compró |
| fechaDescarga | fecha y hora, puede ser nula | Compra.fechaDescarga | Saber si ya se descargó, lo que bloquea el reembolso |


## 4. SolicitarReembolsoRequest
Caso de uso: SolicitarReembolso. Operación del dominio: Compra.solicitarReembolso(motivo, ahora).

| Campo | Tipo | Mapea a | Por qué es necesario |
|---|---|---|---|
| motivo | String | Parámetro motivo de solicitarReembolso | El dominio exige un motivo no vacío |

La compra que se quiere reembolsar va en la ruta (/api/compras/{id}/reembolso) y se carga con CompraRepository.


## 5. RepuestoResponse
Se arma a partir de la entidad Repuesto. Lo devuelven BuscarRepuestosCompatibles, ListarRepuestos y PublicarRepuesto.

| Campo | Tipo | Viene de | Por qué es necesario |
|---|---|---|---|
| repuestoId | String | Repuesto.id | Identificar el repuesto, por ejemplo para comprarlo |
| vendedorId | String | Repuesto.vendedorId | Saber quién lo publicó |
| nombre | String | Repuesto.nombre | Mostrarlo en el catálogo |
| monto y moneda | BigDecimal y String | Repuesto.precio | Mostrar el precio vigente |
| mesesGarantia | int | Repuesto.garantia.mesesCobertura | La garantía es parte de lo que el comprador evalúa |
| modelosCompatibles | Lista de ModeloResponse | Repuesto.compatibilidad | Mostrar con qué motos sirve |


## 6. PaginaResponse
Lo devuelve ListarRepuestos. Se arma a partir de la Pagina del dominio.

| Campo | Tipo | Viene de | Por qué es necesario |
|---|---|---|---|
| contenido | Lista de RepuestoResponse | Pagina.contenido | Los repuestos de la página pedida |
| totalElementos | long | Pagina.totalElementos | Saber cuántos repuestos hay en total |
| totalPaginas | int | Pagina.totalPaginas | Saber cuántas páginas hay |
| pagina y tamano | int | Pagina | Saber qué página se está viendo y de cuántos elementos |


## 7. VendedorResponse
Lo devuelve VerificarEspecialidadVendedor.

| Campo | Tipo | Viene de | Por qué es necesario |
|---|---|---|---|
| vendedorId | String | VendedorEspecializado.id | Identificar al vendedor |
| nombre | String | VendedorEspecializado.nombre | Mostrarlo |
| especialidad | String | VendedorEspecializado.especialidad | Saber en qué se especializa |
| verificado | boolean | VendedorEspecializado.verificado | Saber si ya puede publicar en categorías de alto riesgo |


## 8. RepuestoMasVendidoResponse
Lo devuelve ConsultarRepuestosMasVendidos. Sale del ranking que hace la base de datos con JOIN, GROUP BY y COUNT.

| Campo | Tipo | Viene de | Por qué es necesario |
|---|---|---|---|
| repuestoId | String | RepuestoMasVendido.repuestoId | Identificar el repuesto |
| nombre | String | RepuestoMasVendido.nombre | Mostrarlo en el ranking |
| unidadesVendidas | long | COUNT de compras completadas | Ordenar el ranking |


## Relación entre casos de uso y DTOs

| Caso de uso | Entrada | Salida | Estado del caso de uso |
|---|---|---|---|
| PublicarRepuesto | PublicarRepuestoRequest | RepuestoResponse | Programado |
| ListarRepuestos | texto, precioMaximo, pagina, tamano | PaginaResponse de RepuestoResponse | Programado |
| BuscarRepuestosCompatibles | Parámetros de consulta del Modelo | Lista de RepuestoResponse | Programado |
| EliminarRepuesto | id en la ruta | sin cuerpo | Programado |
| VerificarEspecialidadVendedor | id en la ruta | VendedorResponse | Programado |
| RealizarCompra | RealizarCompraRequest | CompraResponse | Programado |
| ConfirmarPago | id en la ruta | CompraResponse | Programado |
| RegistrarDescarga | id en la ruta | CompraResponse | Programado |
| SolicitarReembolso | SolicitarReembolsoRequest | CompraResponse | Programado |
| ConsultarHistorialDeCompras | nada (el comprador sale del token) | Lista de CompraResponse | Programado |
| ConsultarRepuestosMasVendidos | limite | Lista de RepuestoMasVendidoResponse | Programado |