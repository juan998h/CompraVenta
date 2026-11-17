import { ModeloResponse } from './modelo-response';

export interface PublicarRepuestoRequest {
  nombre: string;
  monto: number;
  moneda: string;
  modelosCompatibles: ModeloResponse[];
  mesesGarantia: number;
  condicionesFabricante: string;
  esCategoriaAltoRiesgo: boolean;
}
