import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { RegistroRequest, UsuarioResponse } from '../models/registro';
import { LoginRequest } from '../models/solicitudes';
import { TokenResponse } from '../models/token-response';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/auth`;

  registrar(datos: RegistroRequest): Observable<UsuarioResponse> {
    return this.http.post<UsuarioResponse>(`${this.url}/registro`, datos);
  }

  login(datos: LoginRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.url}/login`, datos);
  }
}
