import { ModeloResponse } from './modelo-response';

export interface RepuestoResponse {
  repuestoId: string;
  vendedorId: string;
  nombre: string;
  monto: number;
  moneda: string;
  mesesGarantia: number;
  modelosCompatibles: ModeloResponse[];
}
