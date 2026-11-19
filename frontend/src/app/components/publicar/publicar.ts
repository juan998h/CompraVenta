import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AvisoService } from '../../services/aviso.service';
import { traducirError } from '../../services/errores';
import { RepuestoService } from '../../services/repuesto.service';

@Component({
  selector: 'app-publicar',
  imports: [ReactiveFormsModule],
  template: `
    <div class="row justify-content-center">
      <div class="col-12 col-lg-8">
        <h2 class="mb-4">Publicar un repuesto</h2>

        <form [formGroup]="formulario" (ngSubmit)="publicar()" novalidate>
          <div class="row g-3">
            <div class="col-12 col-md-8">
              <label for="nombre" class="form-label">Nombre</label>
              <input
                id="nombre"
                class="form-control"
                formControlName="nombre"
                [class.is-invalid]="invalido('nombre')"
              />
              <div class="invalid-feedback">
                El nombre es obligatorio (minimo 3 letras).
              </div>
            </div>

            <div class="col-12 col-md-4">
              <label for="monto" class="form-label">Precio (COP)</label>
              <input
                id="monto"
                type="number"
                class="form-control"
                formControlName="monto"
                [class.is-invalid]="invalido('monto')"
              />
              <div class="invalid-feedback">
                El precio debe ser mayor a cero.
              </div>
            </div>

            <div class="col-12 col-md-4">
              <label for="mesesGarantia" class="form-label">
                Meses de garantia
              </label>
              <input
                id="mesesGarantia"
                type="number"
                class="form-control"
                formControlName="mesesGarantia"
                [class.is-invalid]="invalido('mesesGarantia')"
              />
              <div class="invalid-feedback">No puede ser negativo.</div>
            </div>

            <div class="col-12 col-md-8">
              <label for="condiciones" class="form-label">
                Condiciones del fabricante
              </label>
              <input
                id="condiciones"
                class="form-control"
                formControlName="condicionesFabricante"
              />
            </div>

            <div class="col-12">
              <div class="form-check">
                <input
                  id="altoRiesgo"
                  type="checkbox"
                  class="form-check-input"
                  formControlName="esCategoriaAltoRiesgo"
                />
                <label for="altoRiesgo" class="form-check-label">
                  Es de categoria de alto riesgo
                </label>
              </div>
            </div>
          </div>

          <h5 class="mt-4">Moto compatible</h5>

          <div class="row g-3">
            <div class="col-12 col-md-6">
              <label for="marca" class="form-label">Marca</label>
              <input
                id="marca"
                class="form-control"
                formControlName="marca"
                [class.is-invalid]="invalido('marca')"
              />
              <div class="invalid-feedback">La marca es obligatoria.</div>
            </div>

            <div class="col-12 col-md-6">
              <label for="version" class="form-label">Modelo</label>
              <input
                id="version"
                class="form-control"
                formControlName="version"
                [class.is-invalid]="invalido('version')"
              />
              <div class="invalid-feedback">El modelo es obligatorio.</div>
            </div>

            <div class="col-12 col-md-6">
              <label for="anio" class="form-label">Ano</label>
              <input
                id="anio"
                type="number"
                class="form-control"
                formControlName="anio"
                [class.is-invalid]="invalido('anio')"
              />
              <div class="invalid-feedback">
                Escribe un ano valido (desde 1900).
              </div>
            </div>

            <div class="col-12 col-md-6">
              <label for="cilindraje" class="form-label">Cilindraje (cc)</label>
              <input
                id="cilindraje"
                type="number"
                class="form-control"
                formControlName="cilindrajeCc"
                [class.is-invalid]="invalido('cilindrajeCc')"
              />
              <div class="invalid-feedback">
                El cilindraje debe ser de 400 cc o mas (alto cilindraje).
              </div>
            </div>
          </div>

          <button
            type="submit"
            class="btn btn-primary mt-4"
            [disabled]="enviando()"
          >
            {{ enviando() ? 'Publicando...' : 'Publicar repuesto' }}
          </button>
        </form>
      </div>
    </div>
  `
})
export class Publicar {
  private fb = inject(FormBuilder);
  private repuestos = inject(RepuestoService);
  private avisos = inject(AvisoService);
  private router = inject(Router);

  enviando = signal(false);

  formulario = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.minLength(3)]],
    monto: [0, [Validators.required, Validators.min(1)]],
    mesesGarantia: [0, [Validators.required, Validators.min(0)]],
    condicionesFabricante: ['Garantia de fabrica'],
    esCategoriaAltoRiesgo: [false],
    marca: ['', Validators.required],
    version: ['', Validators.required],
    anio: [2024, [Validators.required, Validators.min(1900)]],
    cilindrajeCc: [0, [Validators.required, Validators.min(400)]]
  });

  invalido(nombre: string): boolean {
    const campo = this.formulario.get(nombre);
    return !!campo && campo.invalid && (campo.touched || campo.dirty);
  }

  publicar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const v = this.formulario.getRawValue();
    this.enviando.set(true);

    this.repuestos.publicar({
      nombre: v.nombre.trim(),
      monto: v.monto,
      moneda: 'COP',
      mesesGarantia: v.mesesGarantia,
      condicionesFabricante: v.condicionesFabricante,
      esCategoriaAltoRiesgo: v.esCategoriaAltoRiesgo,
      modelosCompatibles: [
        {
          marca: v.marca,
          version: v.version,
          anio: v.anio,
          cilindrajeCc: v.cilindrajeCc
        }
      ]
    }).subscribe({
      next: (repuesto) => {
        this.enviando.set(false);
        this.avisos.exito(`Publicaste "${repuesto.nombre}" correctamente.`);
        this.router.navigate(['/catalogo']);
      },
      error: (error: HttpErrorResponse) => {
        this.enviando.set(false);
        this.avisos.error(traducirError(error));
      }
    });
  }
}
