import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { RepuestoResponse } from '../models/repuesto-response';

@Injectable({ providedIn: 'root' })
export class FavoritoService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/favoritos`;

  listar(): Observable<RepuestoResponse[]> {
    return this.http.get<RepuestoResponse[]>(this.url);
  }

  agregar(repuestoId: string): Observable<void> {
    return this.http.post<void>(`${this.url}/${repuestoId}`, {});
  }

  quitar(repuestoId: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${repuestoId}`);
  }
}
