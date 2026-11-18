import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AvisoService } from '../services/aviso.service';
import { SesionService } from '../services/sesion.service';

// Se escribe una sola vez y actua sobre TODAS las peticiones HTTP
export const autenticacionInterceptor: HttpInterceptorFn = (peticion, siguiente) => {
  const sesion = inject(SesionService);
  const avisos = inject(AvisoService);

  const token = sesion.token();
  const conToken = token
    ? peticion.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : peticion;

  return siguiente(conToken).pipe(
    catchError((error: HttpErrorResponse) => {
      // Un 401 en el login solo significa credenciales malas, no sesion vencida
      const esLogin = peticion.url.includes('/auth/login');
      if (error.status === 401 && !esLogin) {
        sesion.cerrarSesion();
        avisos.error('Tu sesion expiro o no es valida. Inicia sesion de nuevo.');
      }
      return throwError(() => error);
    })
  );
};
