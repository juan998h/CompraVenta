# Casos de uso

Un caso de uso recibe una intencion, busca lo que necesita en el Repository,
invoca comportamiento del dominio, guarda y devuelve el resultado.
No contiene reglas de negocio propias: esas viven en las entidades y en los servicios de dominio.

Estado:
- Programado: existe en main.
- Disenado: esta documentado, pero todavia no esta programado.

| # | Caso de uso | Actor | Que hace | Repository que necesita | Comportamiento del dominio que invoca | Estado |
|---|---|---|---|---|---|---|
| 1 | PublicarRepuesto | Vendedor | Publica un repuesto en el catalogo | VendedorRepository y RepuestoRepository | VendedorEspecializado.publicarRepuesto() | Programado |
| 2 | VerificarEspecialidadVendedor | Plataforma | Marca a un vendedor como verificado | VendedorRepository | VendedorEspecializado.verificarEspecialidad() | Programado |
| 3 | EliminarRepuesto | Vendedor | Elimina logicamente un repuesto propio | RepuestoRepository y CompraRepository | ServicioDeEliminacionDeRepuestos.eliminar() | Disenado |
| 4 | BuscarRepuestosCompatibles | Comprador | Lista los repuestos disponibles para su modelo de moto | RepuestoRepository | Repuesto.esCompatibleCon() | Disenado |
| 5 | RealizarCompra | Comprador | Crea una compra pendiente de un repuesto | RepuestoRepository y CompraRepository | ServicioDeCompra.realizarCompra() | Disenado |
| 6 | ConfirmarPago | Comprador | Completa la compra tras el pago simulado | CompraRepository | Compra.completar() | Disenado |
| 7 | RegistrarDescarga | Comprador | Registra que el archivo fue descargado | CompraRepository | Compra.registrarDescarga() | Programado |
| 8 | SolicitarReembolso | Comprador | Pide el reembolso de una compra | CompraRepository | Compra.solicitarReembolso() | Programado |
| 9 | ConsultarHistorialDeCompras | Comprador | Lista las compras de un comprador | CompraRepository (metodo nuevo: obtenerPorComprador) | Ninguno, solo consulta | Disenado |

