import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { VendedorResponse } from '../../models/vendedor-response';
import { AvisoService } from '../../services/aviso.service';
import { traducirError } from '../../services/errores';
import { VendedorService } from '../../services/vendedor.service';

@Component({
  selector: 'app-verificar-vendedor',
  template: `
    <h2 class="mb-4">Verificar vendedor</h2>
    <p>
      Un vendedor verificado puede publicar repuestos de categorias de alto riesgo.
      Escribe su identificador, por ejemplo vendedor2.
    </p>

    <div class="row g-2 mb-4">
      <div class="col-12 col-md-6">
        <label for="vendedor-id" class="form-label">Id del vendedor</label>
        <input #id id="vendedor-id" class="form-control"
               placeholder="Id del vendedor"
               (keyup.enter)="verificar(id.value)" />
      </div>
      <div class="col-12 col-md-3 d-flex align-items-end">
        <button class="btn btn-success w-100"
                [disabled]="verificando()"
                (click)="verificar(id.value)">
          {{ verificando() ? 'Verificando...' : 'Verificar' }}
        </button>
      </div>
    </div>

    @if (resultado(); as v) {
      <div class="card">
        <div class="card-body">
          <h5 class="card-title">{{ v.nombre }}</h5>
          <p class="mb-1">Id: {{ v.vendedorId }}</p>
          <p class="mb-1">Especialidad: {{ v.especialidad }}</p>
          <span class="badge text-bg-success">
            {{ v.verificado ? 'VERIFICADO' : 'SIN VERIFICAR' }}
          </span>
        </div>
      </div>
    }
  `
})
export class VerificarVendedor {
  private vendedores = inject(VendedorService);
  private avisos = inject(AvisoService);

  verificando = signal(false);
  resultado = signal<VendedorResponse | null>(null);

  verificar(id: string): void {
    if (this.verificando()) return;

    const limpio = id.trim();
    if (!limpio) {
      this.avisos.error('Escribe el id del vendedor.');
      return;
    }

    this.resultado.set(null);
    this.verificando.set(true);

    this.vendedores.verificar(limpio).subscribe({
      next: (vendedor) => {
        this.resultado.set(vendedor);
        this.verificando.set(false);
        this.avisos.exito('Vendedor verificado.');
      },
      error: (error: HttpErrorResponse) => {
        this.verificando.set(false);
        this.avisos.error(traducirError(error));
      }
    });
  }
}