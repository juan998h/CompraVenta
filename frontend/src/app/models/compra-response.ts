export type EstadoCompra = 'PENDIENTE' | 'COMPLETADA' | 'REEMBOLSADA';

export interface CompraResponse {
  compraId: string;
  repuestoId: string;
  compradorId: string;
  monto: number;
  moneda: string;
  estado: EstadoCompra;
  fechaCompra: string;
  fechaDescarga: string | null;
}
