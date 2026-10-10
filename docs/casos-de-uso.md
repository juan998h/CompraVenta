# Casos de uso

Un caso de uso recibe una intención, busca lo que necesita en el Repository,
invoca comportamiento del dominio, guarda y devuelve el resultado.
No contiene reglas de negocio propias: esas viven en las entidades y en los servicios de dominio.

Estado:
- Programado: existe en main.
- Diseñado: está documentado, pero todavía no está programado.

| # | Caso de uso | Actor | Qué hace | Repository que necesita | Comportamiento del dominio que invoca | Estado |
|---|---|---|---|---|---|---|
| 1 | PublicarRepuesto | Vendedor | Publica un repuesto en el catálogo | VendedorRepository y RepuestoRepository | VendedorEspecializado.publicarRepuesto() | Programado |
| 2 | VerificarEspecialidadVendedor | Plataforma | Marca a un vendedor como verificado | VendedorRepository | VendedorEspecializado.verificarEspecialidad() | Programado |
| 3 | EliminarRepuesto | Vendedor | Elimina lógicamente un repuesto propio | RepuestoRepository y CompraRepository | ServicioDeEliminacionDeRepuesto.eliminar() | Programado |
| 4 | BuscarRepuestosCompatibles | Comprador | Lista los repuestos disponibles para su modelo de moto | RepuestoRepository | Repuesto.esCompatibleCon() (a través del repositorio) | Programado |
| 5 | RealizarCompra | Comprador | Crea una compra pendiente de un repuesto | RepuestoRepository y CompraRepository | ServicioDeCompra.realizarCompra() | Programado |
| 6 | ConfirmarPago | Comprador | Completa la compra tras el pago simulado | CompraRepository | Compra.completar() | Programado |
| 7 | RegistrarDescarga | Comprador | Registra que el archivo fue descargado | CompraRepository | Compra.registrarDescarga() | Programado |
| 8 | SolicitarReembolso | Comprador | Pide el reembolso de una compra | CompraRepository | Compra.solicitarReembolso() | Programado |
| 9 | ConsultarHistorialDeCompras | Comprador | Lista las compras de un comprador | CompraRepository (método nuevo: obtenerPorComprador) | Ninguno, solo consulta | Diseñado |
