import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CompraResponse } from '../models/compra-response';
import { RealizarCompraRequest, SolicitarReembolsoRequest } from '../models/solicitudes';

@Injectable({ providedIn: 'root' })
export class CompraService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/compras`;

  comprar(datos: RealizarCompraRequest): Observable<CompraResponse> {
    return this.http.post<CompraResponse>(this.url, datos);
  }

  pendientes(): Observable<CompraResponse[]> {
    return this.http.get<CompraResponse[]>(`${this.url}/pendientes`);
  }

  misCompras(): Observable<CompraResponse[]> {
    return this.http.get<CompraResponse[]>(`${this.url}/mias`);
  }

  confirmar(id: string): Observable<CompraResponse> {
    return this.http.put<CompraResponse>(`${this.url}/${id}/confirmar`, {});
  }

  registrarDescarga(id: string): Observable<CompraResponse> {
    return this.http.put<CompraResponse>(`${this.url}/${id}/descarga`, {});
  }

  reembolsar(id: string, datos: SolicitarReembolsoRequest): Observable<CompraResponse> {
    return this.http.put<CompraResponse>(`${this.url}/${id}/reembolso`, datos);
  }
}
