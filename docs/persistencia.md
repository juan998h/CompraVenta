# Persistencia

Los agregados Repuesto y Compr y el vendedor se guardan en una base de datos H2 con JPA.
El dominio no sabe que existe JPA: las clases del dominio no tienen ninguna anotacion.

## Entidad de dominio y entidad JPA

| Dominio (domain/entity) | JPA (infrastructure/persistence/jpa/entity) | Mapper |
|---|---|---|
| Repuesto | RepuestoEntity, con GarantiaEmbeddable, PrecioEmbeddable y ModeloEmbeddable | RepuestoPersistenceMapper |
| Compra | CompraEntity, con PrecioEmbeddable | CompraPersistenceMapper |
| VendedorEspecializado | VendedorEntity | VendedorPersistenceMapper |

Son clases distintas a proposito. La de dominio protege las reglas (constructor privado, metodos de negocio);
la de JPA solo describe tablas y columnas. El mapper convierte una en otra, y para "revivir" un objeto del
dominio usa el metodo reconstruir(...), que no valida porque esos datos ya pasaron por la fabrica cuando nacieron.

## Tablas

| Tabla | Que guarda |
|---|---|
| repuestos | un repuesto: precio, garantia y la marca eliminado |
| repuesto_modelos | los modelos de moto compatibles de cada repuesto (una fila por modelo) |
| vendedores | los vendedores y si estan verificados |
| compras | las compras, con el precio pagado congelado |

## Repositorios

La interfaz esta en domain/repository y la implementacion con JPA en infrastructure/persistence/jpa:

| Puerto (domain) | Implementacion JPA | Implementacion en memoria (para pruebas) |
|---|---|---|
| RepuestoRepository | RepuestoRepositoryJpa | RepuestoRepositoryEnMemoria |
| CompraRepository | CompraRepositoryJpa | CompraRepositoryEnMemoria |
| VendedorRepository | VendedorRepositoryJpa | VendedorRepositoryEnMemoria |

## Eliminacion logica

Eliminar un repuesto no borra la fila: pone eliminado = true. Los listados filtran por eliminado = false,
asi que un repuesto eliminado no se muestra pero sigue en la base de datos.

## Consultas (JPQL con parametros con nombre)

- Listado paginado de repuestos: busca por texto en el nombre y filtra por precio maximo. La base de datos hace el
  filtro, el conteo y el corte de pagina. Devuelve el contenido, el total de elementos y el total de paginas.
  El dominio recibe una Pagina propia, no la Page de Spring: la conversion se hace en RepuestoRepositoryJpa.
- Repuestos compatibles con un modelo: une repuestos con repuesto_modelos.
- Repuestos mas vendidos: JOIN de compras con repuestos, GROUP BY repuesto, COUNT de compras completadas.
- Compradores con mas reembolsos: GROUP BY comprador, COUNT de compras reembolsadas.
- Historial de un comprador: sus compras de la mas reciente a la mas antigua.

## Datos de prueba

data.sql carga al arrancar 2 vendedores, 6 repuestos (uno eliminado), sus modelos compatibles y 16 compras.
