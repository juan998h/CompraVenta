import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { RepuestoResponse } from '../../models/repuesto-response';
import { AvisoService } from '../../services/aviso.service';
import { traducirError } from '../../services/errores';
import { FavoritoService } from '../../services/favorito.service';

@Component({
  selector: 'app-favoritos',
  imports: [CurrencyPipe],
  template: `
    <h2 class="mb-4">Mis favoritos</h2>

    @if (hayError()) {
      <div class="alert alert-danger">
        No se pudieron cargar tus favoritos.
        <button
          class="btn btn-sm btn-outline-danger ms-2"
          (click)="cargar()"
        >Reintentar</button>
      </div>
    } @else if (lista(); as repuestos) {
      @if (repuestos.length === 0) {
        <div class="alert alert-info">
          Aun no tienes repuestos favoritos.
        </div>
      } @else {
        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
          @for (r of repuestos; track r.repuestoId) {
            <div class="col">
              <div class="card h-100 shadow-sm">
                <div class="card-body">
                  <h5 class="card-title">{{ r.nombre }}</h5>
                  <p class="fs-5 fw-bold mb-1">
                    {{ r.monto | currency: r.moneda : 'symbol-narrow' : '1.0-0' }}
                  </p>
                  <p class="text-muted small">
                    {{ r.mesesGarantia }} meses de garantia
                  </p>
                </div>
                <div class="card-footer bg-transparent border-0 pb-3">
                  <button
                    class="btn btn-outline-danger w-100"
                    (click)="quitar(r)"
                  >
                    Quitar de favoritos
                  </button>
                </div>
              </div>
            </div>
          }
        </div>
      }
    } @else {
      <div class="text-center my-5">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Cargando...</span>
        </div>
      </div>
    }
  `
})
export class Favoritos {
  private servicio = inject(FavoritoService);
  private avisos = inject(AvisoService);

  lista = signal<RepuestoResponse[] | null>(null);
  hayError = signal(false);

  constructor() {
    this.cargar();
  }

  cargar(): void {
    this.hayError.set(false);
    this.servicio.listar().subscribe({
      next: (repuestos) => this.lista.set(repuestos),
      error: () => this.hayError.set(true)
    });
  }

  quitar(repuesto: RepuestoResponse): void {
    this.servicio.quitar(repuesto.repuestoId).subscribe({
      next: () => {
        this.lista.update((actual) =>
          (actual ?? []).filter(
            (r) => r.repuestoId !== repuesto.repuestoId
          )
        );
        this.avisos.exito('Quitado de tus favoritos.');
      },
      error: (error: HttpErrorResponse) =>
        this.avisos.error(traducirError(error))
    });
  }
}
