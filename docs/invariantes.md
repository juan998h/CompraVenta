# Agregados e invariantes

Una invariante es una regla que siempre debe cumplirse. Cada una se protege dentro del dominio
y se verifica con una prueba. 

## Agregado Repuesto

- **Raíz:** Repuesto.
- **Dentro del límite:** Precio, Compatibilidad (con sus Modelo y Cilindraje) y Garantia.
- **Fuera del límite (por id):** VendedorEspecializado, referenciado con vendedorId.

| # | Invariante | Dónde se protege | Prueba |
|---|---|---|---|
| 1 | Un repuesto nunca puede publicarse sin al menos un modelo compatible. | Repuesto.publicar() (constructor privado) | RepuestoTest: noPermiteCrearRepuestoSinModeloCompatible |
| 2 | Un repuesto nunca puede publicarse sin precio ni sin garantía. | Repuesto.publicar() (constructor privado) | RepuestoTest: noPermiteCrearRepuestoSinPrecio y noPermiteCrearRepuestoSinGarantia |
| 3 | El precio nunca puede ser menor o igual a cero. | Precio (record, validación en el constructor) | PrecioTest: noPermitePrecioMenorOIgualACero |
| 4 | Un repuesto eliminado nunca puede eliminarse otra vez. | Repuesto.eliminarLogicamente() | RepuestoTest: noDebePermitirEliminarUnRepuestoYaEliminado (comprueba la excepción y que el estado no cambió) |
| 5 | Un repuesto eliminado nunca puede venderse. | Repuesto.venderUnidad() | RepuestoTest: noDebePermitirVenderUnRepuestoEliminado (comprueba la excepción y que el estado no cambió) |
| 6 | Un repuesto nunca puede existir sin identificador, vendedor y nombre. | Repuesto.publicar() (constructor privado) | Sin prueba propia (protegida en el código) |

## Agregado Compra

- **Raíz:** Compra.
- **Dentro del límite:** Precio (el precio pagado, copiado al momento de comprar) y EstadoCompra.
- **Fuera del límite (por id):** Repuesto, referenciado con repuestoId, y el comprador, con compradorId.

| # | Invariante | Dónde se protege | Prueba |
|---|---|---|---|
| 1 | Una compra nunca puede crearse sin identificador, repuesto, comprador, precio y fecha. | Compra.realizar() (constructor privado) | CompraTest: noPermiteCrearCompraSinComprador (cubre el comprador; los demás campos no tienen prueba propia) |
| 2 | Una compra nunca puede reembolsarse si no está COMPLETADA. | Compra.solicitarReembolso() | CompraTest: noDebePermitirReembolsarUnaCompraPendiente (comprueba la excepción y que el estado no cambió) |
| 3 | Una compra nunca puede reembolsarse después de 5 días desde la fecha de compra. | Compra.solicitarReembolso() y la constante Compra.PLAZO_REEMBOLSO_DIAS | CompraTest: noDebePermitirReembolsarFueraDelPlazo (comprueba la excepción y que el estado no cambió) |
| 4 | Una compra nunca puede reembolsarse si su archivo ya fue descargado. | Compra.solicitarReembolso() | CompraTest: noDebePermitirReembolsarUnaCompraCuyoArchivoYaFueDescargado (comprueba la excepción, el estado y que la descarga sigue registrada) |
| 5 | Una compra nunca puede descargarse si no está COMPLETADA. | Compra.registrarDescarga() | CompraTest: noDebePermitirDescargarUnaCompraPendiente y noDebePermitirDescargarUnaCompraReembolsada |
| 6 | El precio pagado nunca cambia después de la compra, aunque cambie el precio del repuesto. | Campo final en Compra, sin setters | Sin prueba propia (protegida por diseño) |
| 7 | Una compra solo puede completarse si está PENDIENTE. | Compra.completar() | Sin prueba propia (protegida en el código) |

## Regla que cruza agregados

| Regla | Dónde se protege | Prueba |
|---|---|---|
| Un vendedor no verificado nunca puede publicar un repuesto de alto riesgo. | VendedorEspecializado.publicarRepuesto() | VendedorEspecializadoTest: noDebePermitirPublicarEnAltoRiesgoAUnVendedorNoVerificado; PublicarRepuestoUseCaseTest: noPermitePublicarEnAltoRiesgoSiElVendedorNoEstaVerificado |
| Un vendedor nunca puede verificarse dos veces. | VendedorEspecializado.verificarEspecialidad() | VendedorEspecializadoTest: noDebePermitirVerificarDosVecesAlMismoVendedor |

Esta regla pertenece a VendedorEspecializado, que es una entidad. 

## Reglas entre agregados, en servicios de dominio

Estas reglas necesitan consultar un Repository, por eso su lugar es un servicio de dominio y no una entidad.

| Regla | Dónde se protege | Prueba |
|---|---|---|
| Un comprador nunca puede tener dos compras activas del mismo repuesto. | ServicioDeCompra.realizarCompra() | ServicioDeCompraTest: noDebePermitirComprarUnRepuestoConUnaCompraActivaDelMismoComprador; permiteComprarDeNuevoUnRepuestoCuyaCompraAnteriorFueReembolsada |
| Un repuesto nunca puede eliminarse mientras tenga compras activas (pendientes o completadas). | ServicioDeEliminacionDeRepuesto.eliminar() | ServicioDeEliminacionDeRepuestoTest: noDebePermitirEliminarUnRepuestoConComprasActivas; permiteEliminarUnRepuestoSinComprasActivas |

## Reglas diseñadas, todavía no integradas

- Un repuesto nunca puede ser eliminado por un vendedor distinto del que lo publicó: ServicioDeEliminacionDeRepuesto.