import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { RepuestoMasVendidoResponse } from '../models/repuesto-mas-vendido-response';

@Injectable({ providedIn: 'root' })
export class ReporteService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/reportes`;

  masVendidos(limite: number): Observable<RepuestoMasVendidoResponse[]> {
    const params = new HttpParams().set('limite', limite);
    return this.http.get<RepuestoMasVendidoResponse[]>(
      `${this.url}/repuestos-mas-vendidos`, { params }
    );
  }
}