import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RepuestoResponse } from '../../models/repuesto-response';
import { AvisoService } from '../../services/aviso.service';
import { traducirError } from '../../services/errores';
import { RepuestoService } from '../../services/repuesto.service';

@Component({
  selector: 'app-compatibles',
  imports: [ReactiveFormsModule, CurrencyPipe],
  template: `
    <h2 class="mb-4">Buscar repuestos por moto</h2>

    <form [formGroup]="formulario" (ngSubmit)="buscar()" novalidate
          class="row g-3 mb-4">
      @for (campo of campos; track campo.nombre) {
        <div class="col-12 col-md-3">
          <label [for]="campo.nombre" class="form-label">{{ campo.etiqueta }}</label>
          <input [id]="campo.nombre" [type]="campo.tipo"
                 class="form-control" [formControlName]="campo.nombre"
                 [class.is-invalid]="invalido(campo.nombre)" />
          <div class="invalid-feedback">{{ campo.error }}</div>
        </div>
      }

      <div class="col-12">
        <button type="submit" class="btn btn-primary" [disabled]="buscando()">
          {{ buscando() ? 'Buscando...' : 'Buscar' }}
        </button>
      </div>
    </form>

    @if (buscando()) {
      <p role="status">Buscando repuestos...</p>
    } @else if (resultados(); as lista) {
      @if (lista.length === 0) {
        <div class="alert alert-info">
          No hay repuestos compatibles con esa moto.
        </div>
      } @else {
        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
          @for (r of lista; track r.repuestoId) {
            <div class="col">
              <div class="card h-100">
                <div class="card-body">
                  <h5 class="card-title">{{ r.nombre }}</h5>
                  <p class="fs-5 fw-bold">
                    {{ r.monto | currency: r.moneda : 'symbol-narrow' : '1.0-0' }}
                  </p>
                  <p class="small mb-0">{{ r.mesesGarantia }} meses de garantia</p>
                </div>
              </div>
            </div>
          }
        </div>
      }
    }
  `
})
export class Compatibles {
  private fb = inject(FormBuilder);
  private repuestos = inject(RepuestoService);
  private avisos = inject(AvisoService);

  buscando = signal(false);
  resultados = signal<RepuestoResponse[] | null>(null);

  readonly campos = [
    { nombre: 'marca', etiqueta: 'Marca', tipo: 'text', error: 'Escribe la marca.' },
    { nombre: 'version', etiqueta: 'Modelo', tipo: 'text', error: 'Escribe el modelo.' },
    { nombre: 'anio', etiqueta: 'Ano', tipo: 'number', error: 'Ano valido desde 1900.' },
    { nombre: 'cilindrajeCc', etiqueta: 'Cilindraje (cc)', tipo: 'number',
      error: 'Debe ser de 400 cc o mas.' }
  ];

  formulario = this.fb.nonNullable.group({
    marca: ['', Validators.required],
    version: ['', Validators.required],
    anio: [2022, [Validators.required, Validators.min(1900)]],
    cilindrajeCc: [0, [Validators.required, Validators.min(400)]]
  });

  invalido(nombre: string): boolean {
    const campo = this.formulario.get(nombre);
    return !!campo && campo.invalid && (campo.touched || campo.dirty);
  }

  buscar(): void {
    if (this.buscando()) return;
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.buscando.set(true);
    this.resultados.set(null);

    this.repuestos.compatibles(this.formulario.getRawValue()).subscribe({
      next: (lista) => {
        this.resultados.set(lista);
        this.buscando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.buscando.set(false);
        this.avisos.error(traducirError(error));
      }
    });
  }
}