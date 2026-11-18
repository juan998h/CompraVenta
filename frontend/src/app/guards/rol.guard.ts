import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AvisoService } from '../services/aviso.service';
import { SesionService } from '../services/sesion.service';

// Permite entrar solo a usuarios con el rol indicado.
export const rolGuard = (rol: string): CanActivateFn => () => {
  const sesion = inject(SesionService);
  const router = inject(Router);
  const avisos = inject(AvisoService);

  if (sesion.tieneRol(rol)) {
    return true;
  }

  avisos.error('No tienes permiso para entrar a esa pagina.');
  return router.createUrlTree(['/catalogo']);
};
