# DTOs (Request y Response)

Un DTO lleva datos entre la API y el dominio. 
No contiene reglas: las reglas las valida el dominio.

## 1. PublicarRepuestoRequest
Caso de uso: PublicarRepuesto. Operación del dominio: VendedorEspecializado.publicarRepuesto(), que crea el Repuesto.

| Campo | Tipo | Mapea a | Por qué es necesario |
|---|---|---|---|
| vendedorId | String | Se carga el VendedorEspecializado con VendedorRepository | Saber quién publica y aplicar la regla de verificación |
| nombre | String | Repuesto.nombre | Todo repuesto debe tener nombre |
| monto | BigDecimal | Precio.monto | El precio debe ser mayor a cero |
| moneda | String (opcional, por defecto COP) | Precio.moneda | El precio lleva moneda |
| modelosCompatibles | Lista de (marca, versión, año, cilindrajeCc) | Compatibilidad, con sus Modelo y Cilindraje | Un repuesto debe declarar al menos un modelo compatible |
| mesesGarantia | int | Garantia.mesesCobertura | La garantía es obligatoria |
| condicionesFabricante | String | Garantia.condicionesFabricante | La garantía depende de las condiciones del fabricante |
| esCategoriaAltoRiesgo | boolean | Parámetro de publicarRepuesto | Decide si se exige que el vendedor esté verificado |



## 2. RealizarCompraRequest
Caso de uso: RealizarCompra. Operación del dominio: ServicioDeCompra.realizarCompra(), que crea la Compra.

| Campo | Tipo | Mapea a | Por qué es necesario |
|---|---|---|---|
| compradorId | String | Compra.compradorId | Saber quién compra |
| repuestoId | String | Se carga el Repuesto y se guarda en Compra.repuestoId | Saber qué se compra |



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
| compraId | String | Se carga la Compra con CompraRepository | Saber cuál compra se quiere reembolsar |
| motivo | String | Parámetro motivo de solicitarReembolso | El dominio exige un motivo no vacío |


## 5. RepuestoResponse
Se arma a partir de la entidad Repuesto. Lo devuelven BuscarRepuestosCompatibles y PublicarRepuesto.

| Campo | Tipo | Viene de | Por qué es necesario |
|---|---|---|---|
| repuestoId | String | Repuesto.id | Identificar el repuesto, por ejemplo para comprarlo |
| vendedorId | String | Repuesto.vendedorId | Saber quién lo publicó |
| nombre | String | Repuesto.nombre | Mostrarlo en el catálogo |
| monto y moneda | BigDecimal y String | Repuesto.precio | Mostrar el precio vigente |
| mesesGarantia | int | Repuesto.garantia.mesesCobertura | La garantía es parte de lo que el comprador evalúa |


## Relación entre casos de uso y DTOs

| Caso de uso | Entrada | Salida | Estado del caso de uso |
|---|---|---|---|
| PublicarRepuesto | PublicarRepuestoRequest | RepuestoResponse | Programado |
| BuscarRepuestosCompatibles | Parámetros de consulta del Modelo | Lista de RepuestoResponse | Diseñado |
| RealizarCompra | RealizarCompraRequest | CompraResponse | Diseñado |
| ConfirmarPago | compraId en la ruta | CompraResponse | Diseñado |
| RegistrarDescarga | compraId en la ruta | CompraResponse | Programado |
| SolicitarReembolso | SolicitarReembolsoRequest | CompraResponse | Programado |
| ConsultarHistorialDeCompras | compradorId en la ruta | Lista de CompraResponse | Diseñado |

Los casos VerificarEspecialidadVendedor y EliminarRepuesto solo reciben identificadores
(vendedorId y repuestoId) y no devuelven datos, por eso no tienen DTO propio.