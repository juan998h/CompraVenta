import { HttpErrorResponse } from '@angular/common/http';

// Convierte los errores HTTP en mensajes para el usuario.
export function traducirError(error: HttpErrorResponse): string {
  switch (error.status) {
    case 0:
      return 'No hay conexion con el servidor. Intenta de nuevo.';
    case 400:
      return 'Revisa los datos que escribiste.';
    case 401:
      return 'Inicia sesion para continuar.';
    case 403:
      return error.error?.mensaje ?? 'No tienes permiso para hacer esto.';
    case 404:
      return 'Lo que buscas no existe.';
    case 409:
    case 422:
      return error.error?.mensaje ?? 'No se pudo completar la operacion.';
    default:
      return 'Ocurrio un error inesperado. Intenta mas tarde.';
  }
}
