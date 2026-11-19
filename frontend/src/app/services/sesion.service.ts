import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { TokenResponse } from '../models/token-response';

const CLAVE_TOKEN = 'bikers_token';
const CLAVE_ROLES = 'bikers_roles';

@Injectable({ providedIn: 'root' })
export class SesionService {
  private router = inject(Router);

  // Signals: la navbar y los guards se actualizan solos cuando cambian
  readonly token = signal<string | null>(this.leerToken());
  readonly roles = signal<string[]>(this.leerRoles());
  readonly haySesionActiva = computed(() => this.token() !== null);
  readonly usuarioId = computed(() => this.leerUsuarioId(this.token()));

  guardarSesion(respuesta: TokenResponse): void {
    localStorage.setItem(CLAVE_TOKEN, respuesta.token);
    localStorage.setItem(CLAVE_ROLES, JSON.stringify(respuesta.roles));
    this.token.set(respuesta.token);
    this.roles.set(respuesta.roles);
  }

  // Cierra la sesion sin recargar la pagina: limpia todo y va al login
  cerrarSesion(): void {
    localStorage.removeItem(CLAVE_TOKEN);
    localStorage.removeItem(CLAVE_ROLES);
    this.token.set(null);
    this.roles.set([]);
    this.router.navigate(['/login']);
  }

  tieneRol(rol: string): boolean {
    return this.roles().includes(rol);
  }

  private leerUsuarioId(token: string | null): string | null {
    if (!token) return null;

    try {
      const cuerpo = token.split('.')[1]
        .replace(/-/g, '+')
        .replace(/_/g, '/');
      const base64 = cuerpo.padEnd(Math.ceil(cuerpo.length / 4) * 4, '=');
      const datos = JSON.parse(atob(base64));
      return typeof datos.sub === 'string' ? datos.sub : null;
    } catch {
      return null;
    }
  }

  private leerToken(): string | null {
    return localStorage.getItem(CLAVE_TOKEN);
  }

  private leerRoles(): string[] {
    try {
      return JSON.parse(localStorage.getItem(CLAVE_ROLES) ?? '[]');
    } catch {
      return [];
    }
  }
}
