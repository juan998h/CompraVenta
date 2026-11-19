import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { VendedorResponse } from '../models/vendedor-response';

@Injectable({ providedIn: 'root' })
export class VendedorService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/vendedores`;

  verificar(id: string): Observable<VendedorResponse> {
    return this.http.put<VendedorResponse>(
      `${this.url}/${encodeURIComponent(id)}/verificar`, {}
    );
  }
}