# Agregados e invariantes

Una invariante es una regla que siempre debe cumplirse. Cada una se protege dentro del dominio
y se verifica con una prueba.

Regla de construcción: ninguna entidad tiene lógica en el constructor. El constructor privado solo asigna
los campos. Las reglas se validan en el método de fábrica (publicar, realizar, registrar) antes de crear el
objeto, y cada entidad tiene un método reconstruir(...) para la persistencia, que no valida porque esos datos
ya pasaron por la fábrica cuando nacieron.

## Agregado Repuesto

- **Raíz:** Repuesto.
- **Dentro del límite:** Precio, Compatibilidad (con sus Modelo y Cilindraje) y Garantia.
- **Fuera del límite (por id):** VendedorEspecializado, referenciado con vendedorId.
- **Comportamiento de la raíz:** publicar, renombrar, cambiarPrecio, actualizarGarantia, agregarModeloCompatible,
  quitarModeloCompatible, eliminarLogicamente, reactivar, venderUnidad, verificarQueLoPuedeEliminar, y las
  consultas estaDisponible, perteneceA, esCompatibleCon y esDeAltoCilindraje.

| # | Invariante | Dónde se protege | Prueba |
|---|---|---|---|
| 1 | Un repuesto nunca puede publicarse sin al menos un modelo compatible. | Repuesto.publicar() (fábrica; el constructor solo asigna) | RepuestoTest: noPermiteCrearRepuestoSinModeloCompatible |
| 2 | Un repuesto nunca puede publicarse sin precio ni sin garantía. | Repuesto.publicar() (fábrica; el constructor solo asigna) | RepuestoTest: noPermiteCrearRepuestoSinPrecio y noPermiteCrearRepuestoSinGarantia |
| 3 | El precio nunca puede ser menor o igual a cero. | Precio (record, validación en el constructor) | PrecioTest: noPermitePrecioMenorOIgualACero |
| 4 | Un repuesto eliminado nunca puede eliminarse otra vez. | Repuesto.eliminarLogicamente() | RepuestoTest: noDebePermitirEliminarUnRepuestoYaEliminado (comprueba la excepción y que el estado no cambió) |
| 5 | Un repuesto eliminado nunca puede venderse. | Repuesto.venderUnidad() | RepuestoTest: noDebePermitirVenderUnRepuestoEliminado (comprueba la excepción y que el estado no cambió) |
| 6 | Un repuesto nunca puede existir sin identificador, vendedor y nombre. | Repuesto.publicar() (fábrica; el constructor solo asigna) | Sin prueba propia (protegida en el código) |
| 7 | Un repuesto eliminado nunca puede modificarse (nombre, precio, garantía ni modelos compatibles). | Repuesto.verificarQueSePuedaModificar(), usado por cada método que modifica | RepuestoComportamientoTest: noDebePermitirCambiarElPrecioDeUnRepuestoEliminado (cubre el cambio de precio; los demás métodos no tienen prueba propia) |
| 8 | El nombre de un repuesto nunca puede quedar vacío. | Repuesto.renombrar() | RepuestoComportamientoTest: noDebePermitirRenombrarConUnNombreVacio (comprueba la excepción y que el nombre no cambió) |
| 9 | Un modelo nunca puede estar repetido en la compatibilidad de un repuesto. | Repuesto.agregarModeloCompatible() | RepuestoComportamientoTest: noDebePermitirAgregarUnModeloQueYaEsCompatible |
| 10 | Un repuesto nunca puede quedar sin ningún modelo compatible. | Repuesto.quitarModeloCompatible() | RepuestoComportamientoTest: noDebePermitirQuitarElUnicoModeloCompatible (comprueba la excepción y que el modelo sigue) |
| 11 | Solo un repuesto eliminado puede reactivarse. | Repuesto.reactivar() | RepuestoComportamientoTest: noDebePermitirReactivarUnRepuestoQueNoEstaEliminado |
| 12 | Solo el vendedor que publicó un repuesto puede eliminarlo. | Repuesto.verificarQueLoPuedeEliminar(), llamado desde ServicioDeEliminacionDeRepuesto.eliminar() | RepuestoComportamientoTest: noDebePermitirEliminarAUnVendedorQueNoEsElDueño; ServicioDeEliminacionDeRepuestoTest: noDebePermitirEliminarUnRepuestoQueNoEsDelVendedor; EliminarRepuestoUseCaseTest: noPermiteQueOtroVendedorElimineElRepuesto |

## Agregado Compra

- **Raíz:** Compra.
- **Dentro del límite:** Precio (el precio pagado, copiado al momento de comprar) y EstadoCompra.
- **Fuera del límite (por id):** Repuesto, referenciado con repuestoId, y el comprador, con compradorId.

| # | Invariante | Dónde se protege | Prueba |
|---|---|---|---|
| 1 | Una compra nunca puede crearse sin identificador, repuesto, comprador, precio y fecha. | Compra.realizar() (fábrica; el constructor solo asigna) | CompraTest: noPermiteCrearCompraSinComprador (cubre el comprador; los demás campos no tienen prueba propia) |
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