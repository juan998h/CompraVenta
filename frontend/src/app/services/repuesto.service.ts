import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ModeloResponse } from '../models/modelo-response';
import { PaginaResponse } from '../models/pagina-response';
import { PublicarRepuestoRequest } from '../models/publicar-repuesto-request';
import { RepuestoResponse } from '../models/repuesto-response';

@Injectable({ providedIn: 'root' })
export class RepuestoService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/repuestos`;

  // La paginacion y la busqueda las hace el backend: aqui solo mandamos los parametros
  listar(
    texto: string,
    precioMaximo: number | null,
    pagina: number,
    tamano: number
  ): Observable<PaginaResponse<RepuestoResponse>> {
    let params = new HttpParams().set('pagina', pagina).set('tamano', tamano);
    if (texto.trim()) {
      params = params.set('texto', texto.trim());
    }
    if (precioMaximo !== null) {
      params = params.set('precioMaximo', precioMaximo);
    }
    return this.http.get<PaginaResponse<RepuestoResponse>>(this.url, { params });
  }

  compatibles(modelo: ModeloResponse): Observable<RepuestoResponse[]> {
    const params = new HttpParams()
      .set('marca', modelo.marca)
      .set('version', modelo.version)
      .set('anio', modelo.anio)
      .set('cilindrajeCc', modelo.cilindrajeCc);
    return this.http.get<RepuestoResponse[]>(`${this.url}/compatibles`, { params });
  }

  publicar(datos: PublicarRepuestoRequest): Observable<RepuestoResponse> {
    return this.http.post<RepuestoResponse>(this.url, datos);
  }

  eliminar(id: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
