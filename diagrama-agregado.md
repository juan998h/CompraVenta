# Invariantes del agregado Repuesto

1. Un Repuesto nunca puede crearse sin al menos un Modelo compatible declarado (su constructor lo valida y lanza ReglaDominioException si no cumple).
2. El precio de un Repuesto siempre debe ser mayor a cero.
3. Un Repuesto eliminado lógicamente (eliminarLogicamente()) nunca vuelve a estar disponible para la venta.
4. La Garantia de un Repuesto siempre corresponde al tipo de producto y a las condiciones establecidas por el fabricante, nunca queda nula tras su creación.