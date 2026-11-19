import { CurrencyPipe, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { CompraResponse } from '../../models/compra-response';
import { AvisoService } from '../../services/aviso.service';
import { CompraService } from '../../services/compra.service';
import { traducirError } from '../../services/errores';

@Component({
  selector: 'app-mis-compras',
  imports: [CurrencyPipe, DatePipe],
  template: `
    <h2 class="mb-4">Mis compras</h2>

    @if (hayError()) {
      <div class="alert alert-danger">
        No se pudieron cargar tus compras.
        <button class="btn btn-sm btn-outline-danger ms-2"
                (click)="cargar()">Reintentar</button>
      </div>
    } @else if (lista(); as compras) {
      @if (compras.length === 0) {
        <div class="alert alert-info">Aun no has comprado nada.</div>
      } @else {
        <div class="row row-cols-1 row-cols-lg-2 g-4">
          @for (c of compras; track c.compraId) {
            <div class="col">
              <div class="card h-100">
                <div class="card-body">
                  <div class="d-flex justify-content-between gap-2 mb-2">
                    <h5 class="card-title">Repuesto: {{ c.repuestoId }}</h5>
                    <span class="badge align-self-start"
                          [class.text-bg-warning]="c.estado === 'PENDIENTE'"
                          [class.text-bg-success]="c.estado === 'COMPLETADA'"
                          [class.text-bg-secondary]="c.estado === 'REEMBOLSADA'">
                      {{ c.estado }}
                    </span>
                  </div>
                  <p class="fs-5 fw-bold">
                    {{ c.monto | currency: c.moneda : 'symbol-narrow' : '1.0-0' }}
                  </p>
                  <p class="small">
                    Comprado el {{ c.fechaCompra | date: 'dd/MM/yyyy HH:mm' }}
                  </p>

                  @if (c.estado === 'PENDIENTE') {
                    <p>Esperando la confirmacion del pago por el administrador.</p>
                  }
                  @if (c.estado === 'REEMBOLSADA') {
                    <p>Esta compra fue reembolsada.</p>
                  }
                  @if (c.estado === 'COMPLETADA' && c.fechaDescarga) {
                    <p>
                      Descargada el {{ c.fechaDescarga | date: 'dd/MM/yyyy HH:mm' }}.
                      Ya no se puede reembolsar.
                    </p>
                  }

                  @if (enReembolso() === c.compraId) {
                    <label class="form-label" [for]="'motivo-' + c.compraId">
                      Motivo del reembolso
                    </label>
                    <textarea #motivo class="form-control mb-2" rows="2"
                              [id]="'motivo-' + c.compraId"></textarea>
                    <div class="d-flex gap-2">
                      <button class="btn btn-danger"
                              [disabled]="ocupado() !== null"
                              (click)="enviarReembolso(c, motivo.value)">
                        {{ ocupado() === c.compraId ? 'Enviando...' : 'Enviar solicitud' }}
                      </button>
                      <button class="btn btn-outline-secondary"
                              [disabled]="ocupado() !== null"
                              (click)="enReembolso.set(null)">Cancelar</button>
                    </div>
                  }
                </div>

                @if (c.estado === 'COMPLETADA' && !c.fechaDescarga &&
                     enReembolso() !== c.compraId) {
                  <div class="card-footer bg-transparent border-0 pb-3 d-flex gap-2">
                    <button class="btn btn-primary flex-grow-1"
                            [disabled]="ocupado() !== null"
                            (click)="descargar(c)">
                      {{ ocupado() === c.compraId ? 'Registrando...' : 'Descargar' }}
                    </button>
                    <button class="btn btn-outline-danger"
                            [disabled]="ocupado() !== null"
                            (click)="enReembolso.set(c.compraId)">
                      Pedir reembolso
                    </button>
                  </div>
                }
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
export class MisCompras {
  private compras = inject(CompraService);
  private avisos = inject(AvisoService);

  lista = signal<CompraResponse[] | null>(null);
  hayError = signal(false);
  enReembolso = signal<string | null>(null);
  ocupado = signal<string | null>(null);

  constructor() {
    this.cargar();
  }

  cargar(): void {
    this.hayError.set(false);
    this.compras.misCompras().subscribe({
      next: (datos) => this.lista.set(datos),
      error: () => this.hayError.set(true)
    });
  }

  descargar(compra: CompraResponse): void {
    if (this.ocupado() !== null) return;
    this.ocupado.set(compra.compraId);

    this.compras.registrarDescarga(compra.compraId).subscribe({
      next: (actualizada) => {
        this.reemplazar(actualizada);
        this.ocupado.set(null);
        this.avisos.exito(
          'Descarga registrada. Ya no podras pedir reembolso de esta compra.'
        );
      },
      error: (error: HttpErrorResponse) => {
        this.ocupado.set(null);
        this.avisos.error(traducirError(error));
      }
    });
  }

  enviarReembolso(compra: CompraResponse, motivo: string): void {
    if (this.ocupado() !== null) return;
    const texto = motivo.trim();

    if (texto.length < 3) {
      this.avisos.error('Escribe un motivo de al menos 3 letras.');
      return;
    }

    this.ocupado.set(compra.compraId);
    this.compras.reembolsar(compra.compraId, { motivo: texto }).subscribe({
      next: (actualizada) => {
        this.reemplazar(actualizada);
        this.ocupado.set(null);
        this.enReembolso.set(null);
        this.avisos.exito('Reembolso solicitado correctamente.');
      },
      error: (error: HttpErrorResponse) => {
        this.ocupado.set(null);
        this.avisos.error(traducirError(error));
      }
    });
  }

  private reemplazar(actualizada: CompraResponse): void {
    this.lista.update((actual) =>
      (actual ?? []).map((c) =>
        c.compraId === actualizada.compraId ? actualizada : c
      )
    );
  }
}