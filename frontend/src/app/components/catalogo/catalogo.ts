import { HttpErrorResponse } from '@angular/common/http';
import { AvisoService } from '../../services/aviso.service';
import { CompraService } from '../../services/compra.service';
import { traducirError } from '../../services/errores';
import { SesionService } from '../../services/sesion.service';

import { CurrencyPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  of,
  skip,
  switchMap,
  tap
} from 'rxjs';
import { PaginaResponse } from '../../models/pagina-response';
import { RepuestoResponse } from '../../models/repuesto-response';
import { RepuestoService } from '../../services/repuesto.service';

interface Consulta {
  texto: string;
  pagina: number;
}

@Component({
  selector: 'app-catalogo',
  imports: [CurrencyPipe],
  template: `
    <h2 class="mb-3">Catalogo de repuestos</h2>

    <input
      #caja
      type="search"
      class="form-control mb-4"
      placeholder="Buscar por nombre..."
      (input)="texto.set(caja.value)"
    />

    @if (cargando()) {
      <div class="text-center my-5">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Cargando...</span>
        </div>
      </div>
    } @else if (hayError()) {
      <div class="alert alert-danger">
        No se pudo cargar el catalogo.
        <button
          class="btn btn-sm btn-outline-danger ms-2"
          (click)="reintentar()"
        >Reintentar</button>
      </div>
    } @else if (resultado(); as pag) {
      @if (pag.contenido.length === 0) {
        <div class="alert alert-info">
          No se encontraron repuestos para tu busqueda.
        </div>
      } @else {
        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
          @for (r of pag.contenido; track r.repuestoId) {
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
                  @for (m of r.modelosCompatibles; track $index) {
                    <span class="badge text-bg-secondary me-1">
                      {{ m.marca }} {{ m.version }}
                    </span>
                  }
                </div>
                @if (sesion.tieneRol('COMPRADOR')) {
                  <div class="card-footer bg-transparent border-0 pb-3">
                    <button
                      class="btn btn-primary w-100"
                      [disabled]="comprando() === r.repuestoId"
                      (click)="comprar(r)"
                    >
                      {{ comprando() === r.repuestoId ? 'Comprando...' : 'Comprar' }}
                    </button>
                  </div>
                }
              </div>
            </div>
          }
        </div>

        <div class="d-flex justify-content-center align-items-center gap-3 mt-4">
          <button
            class="btn btn-outline-primary"
            [disabled]="pag.pagina === 0"
            (click)="irAPagina(pag.pagina - 1)"
          >Anterior</button>

          <span>Pagina {{ pag.pagina + 1 }} de {{ pag.totalPaginas }}</span>

          <button
            class="btn btn-outline-primary"
            [disabled]="pag.pagina + 1 >= pag.totalPaginas"
            (click)="irAPagina(pag.pagina + 1)"
          >Siguiente</button>
        </div>
      }
    }
  `
})
export class Catalogo {
  private repuestos = inject(RepuestoService);
  private compras = inject(CompraService);
  private avisos = inject(AvisoService);
  sesion = inject(SesionService);

  comprando = signal<string | null>(null);

  readonly tamano = 3;

  texto = signal('');
  consulta = signal<Consulta>({ texto: '', pagina: 0 });

  resultado = signal<PaginaResponse<RepuestoResponse> | null>(null);
  cargando = signal(false);
  hayError = signal(false);

  constructor() {
    toObservable(this.texto)
      .pipe(
        skip(1),
        debounceTime(400),
        distinctUntilChanged(),
        takeUntilDestroyed()
      )
      .subscribe((texto) => this.consulta.set({ texto, pagina: 0 }));

    toObservable(this.consulta)
      .pipe(
        tap(() => {
          this.cargando.set(true);
          this.hayError.set(false);
        }),
        switchMap((c) =>
          this.repuestos.listar(c.texto, null, c.pagina, this.tamano).pipe(
            catchError(() => {
              this.hayError.set(true);
              return of(null);
            })
          )
        ),
        takeUntilDestroyed()
      )
      .subscribe((respuesta) => {
        this.resultado.set(respuesta);
        this.cargando.set(false);
      });
  }

  irAPagina(pagina: number): void {
    this.consulta.update((c) => ({ ...c, pagina }));
  }

  comprar(repuesto: RepuestoResponse): void {
    this.comprando.set(repuesto.repuestoId);

    this.compras.comprar({ repuestoId: repuesto.repuestoId }).subscribe({
      next: () => {
        this.comprando.set(null);
        this.avisos.exito(
          `Compraste "${repuesto.nombre}". Queda pendiente de confirmar el pago.`
        );
      },
      error: (error: HttpErrorResponse) => {
        this.comprando.set(null);
        this.avisos.error(traducirError(error));
      }
    });
  }

  reintentar(): void {
    this.consulta.update((c) => ({ ...c }));
  }
}

