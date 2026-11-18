import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AvisoService } from '../services/aviso.service';
import { SesionService } from '../services/sesion.service';

export const autenticadoGuard: CanActivateFn = () => {
  const sesion = inject(SesionService);
  const router = inject(Router);
  const avisos = inject(AvisoService);

  if (sesion.haySesionActiva()) {
    return true;
  }

  avisos.info('Inicia sesion para entrar a esa pagina.');
  return router.createUrlTree(['/login']);
};
