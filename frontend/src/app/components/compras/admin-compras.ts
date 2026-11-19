import { CurrencyPipe, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { CompraResponse } from '../../models/compra-response';
import { AvisoService } from '../../services/aviso.service';
import { CompraService } from '../../services/compra.service';
import { traducirError } from '../../services/errores';

@Component({
  selector: 'app-admin-compras',
  imports: [CurrencyPipe, DatePipe],
  template: `
    <h2 class="mb-4">Compras pendientes de confirmar</h2>

    @if (hayError()) {
      <div class="alert alert-danger">
        No se pudieron cargar las compras.
        <button class="btn btn-sm btn-outline-danger ms-2" (click)="cargar()">
          Reintentar
        </button>
      </div>
    } @else if (lista(); as compras) {
      @if (compras.length === 0) {
        <div class="alert alert-info">No hay compras pendientes.</div>
      } @else {
        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
          @for (c of compras; track c.compraId) {
            <div class="col">
              <div class="card h-100">
                <div class="card-body">
                  <h5 class="card-title">Repuesto: {{ c.repuestoId }}</h5>
                  <p class="mb-1">Comprador: <strong>{{ c.compradorId }}</strong></p>
                  <p class="fs-5 fw-bold mb-1">
                    {{ c.monto | currency: c.moneda : 'symbol-narrow' : '1.0-0' }}
                  </p>
                  <p class="small mb-0">
                    {{ c.fechaCompra | date: 'dd/MM/yyyy HH:mm' }}
                  </p>
                </div>
                <div class="card-footer bg-transparent border-0 pb-3">
                  <button
                    class="btn btn-success w-100"
                    [disabled]="confirmando() === c.compraId"
                    (click)="confirmar(c)"
                  >
                    {{ confirmando() === c.compraId ? 'Confirmando...' : 'Confirmar pago' }}
                  </button>
                </div>
              </div>
            </div>
          }
        </div>
      }
    }
  `
})
export class AdminCompras {
  private compras = inject(CompraService);
  private avisos = inject(AvisoService);

  lista = signal<CompraResponse[] | null>(null);
  hayError = signal(false);
  confirmando = signal<string | null>(null);

  constructor() {
    this.cargar();
  }

  cargar(): void {
    this.hayError.set(false);
    this.compras.pendientes().subscribe({
      next: (datos) => this.lista.set(datos),
      error: () => this.hayError.set(true)
    });
  }

  confirmar(compra: CompraResponse): void {
    this.confirmando.set(compra.compraId);
    this.compras.confirmar(compra.compraId).subscribe({
      next: () => {
        this.lista.update((actual) =>
          (actual ?? []).filter((c) => c.compraId !== compra.compraId)
        );
        this.confirmando.set(null);
        this.avisos.exito('Pago confirmado. La compra quedo COMPLETADA.');
      },
      error: (error: HttpErrorResponse) => {
        this.confirmando.set(null);
        this.avisos.error(traducirError(error));
      }
    });
  }
}