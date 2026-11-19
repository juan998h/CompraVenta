import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AvisoService } from '../services/aviso.service';
import { SesionService } from '../services/sesion.service';

export const algunRolGuard = (...roles: string[]): CanActivateFn => () => {
  const sesion = inject(SesionService);
  const router = inject(Router);
  const avisos = inject(AvisoService);

  if (roles.some((rol) => sesion.tieneRol(rol))) {
    return true;
  }

  avisos.error('No tienes permiso para entrar a esa pagina.');
  return router.createUrlTree(['/catalogo']);
};