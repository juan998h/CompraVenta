import { Component, inject } from '@angular/core';
import { AvisoService } from '../../services/aviso.service';

@Component({
  selector: 'app-avisos',
  template: `
    <div class="position-fixed top-0 end-0 p-3" style="z-index: 1080; max-width: 400px">
      @for (aviso of servicio.avisos(); track aviso.id) {
        <div class="alert alert-{{ aviso.tipo }} alert-dismissible shadow-sm" role="alert">
          {{ aviso.mensaje }}
          <button
            type="button"
            class="btn-close"
            aria-label="Cerrar"
            (click)="servicio.cerrar(aviso.id)"
          ></button>
        </div>
      }
    </div>
  `
})
export class Avisos {
  servicio = inject(AvisoService);
}
