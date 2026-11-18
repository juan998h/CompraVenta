import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SesionService } from '../services/sesion.service';

export const invitadoGuard: CanActivateFn = () => {
  const sesion = inject(SesionService);
  const router = inject(Router);

  if (!sesion.haySesionActiva()) {
    return true;
  }

  return router.createUrlTree(['/catalogo']);
};
