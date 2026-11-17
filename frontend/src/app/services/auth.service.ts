import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { LoginRequest } from '../models/solicitudes';
import { TokenResponse } from '../models/token-response';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/auth`;

  login(datos: LoginRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.url}/login`, datos);
  }
}
