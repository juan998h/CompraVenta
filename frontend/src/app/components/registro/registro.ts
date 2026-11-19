import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AvisoService } from '../../services/aviso.service';
import { traducirError } from '../../services/errores';

@Component({
  selector: 'app-registro',
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="row justify-content-center">
      <div class="col-12 col-md-8 col-lg-5">
        <h2 class="mb-4 text-center">Crear cuenta</h2>

        <form [formGroup]="formulario" (ngSubmit)="registrar()" novalidate>
          <div class="mb-3">
            <label for="nombre" class="form-label">Nombre</label>
            <input id="nombre" class="form-control" formControlName="nombre"
                   [class.is-invalid]="invalido('nombre')" />
            <div class="invalid-feedback">El nombre es obligatorio.</div>
          </div>

          <div class="mb-3">
            <label for="correo" class="form-label">Correo</label>
            <input id="correo" type="email" class="form-control"
                   formControlName="correo"
                   [class.is-invalid]="invalido('correo')" />
            <div class="invalid-feedback">Escribe un correo valido.</div>
          </div>

          <div class="mb-3">
            <label for="contrasena" class="form-label">Contrasena</label>
            <input id="contrasena" type="password" class="form-control"
                   formControlName="contrasena"
                   [class.is-invalid]="invalido('contrasena')" />
            <div class="invalid-feedback">
              Minimo 8 caracteres, una mayuscula y un numero.
            </div>
          </div>

          <div class="mb-3">
            <label for="rol" class="form-label">Quiero registrarme como</label>
            <select id="rol" class="form-select" formControlName="rol">
              <option value="COMPRADOR">Comprador</option>
              <option value="VENDEDOR">Vendedor</option>
            </select>
          </div>

          @if (formulario.controls.rol.value === 'VENDEDOR') {
            <div class="mb-3">
              <label for="especialidad" class="form-label">Especialidad</label>
              <select id="especialidad" class="form-select"
                      formControlName="especialidad"
                      [class.is-invalid]="invalido('especialidad')">
                <option value="">Selecciona una</option>
                @for (e of especialidades; track e) {
                  <option [value]="e">{{ e }}</option>
                }
              </select>
              <div class="invalid-feedback">Elige tu especialidad.</div>
            </div>
          }

          @if (errorRegistro()) {
            <div class="alert alert-danger" role="alert">
              {{ errorRegistro() }}
            </div>
          }

          <button type="submit" class="btn btn-primary w-100"
                  [disabled]="enviando()">
            {{ enviando() ? 'Creando cuenta...' : 'Crear cuenta' }}
          </button>
        </form>

        <p class="text-center mt-3 mb-0">
          Ya tienes cuenta? <a routerLink="/login">Inicia sesion</a>
        </p>
      </div>
    </div>
  `
})
export class Registro {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private avisos = inject(AvisoService);
  private router = inject(Router);

  readonly especialidades = [
    'SUSPENSION', 'ECU', 'MANEJO', 'HOMOLOGACION',
    'MANTENIMIENTO', 'REPUESTOS_ORIGINALES'
  ];

  enviando = signal(false);
  errorRegistro = signal<string | null>(null);

  formulario = this.fb.nonNullable.group({
    nombre: ['', Validators.required],
    correo: ['', [Validators.required, Validators.email]],
    contrasena: ['', [
      Validators.required,
      Validators.minLength(8),
      Validators.pattern(/^(?=.*[A-Z])(?=.*\d).*$/)
    ]],
    rol: ['COMPRADOR'],
    especialidad: ['']
  });

  constructor() {
    this.formulario.controls.rol.valueChanges
      .pipe(takeUntilDestroyed())
      .subscribe((rol) => {
        const especialidad = this.formulario.controls.especialidad;
        if (rol === 'VENDEDOR') {
          especialidad.setValidators(Validators.required);
        } else {
          especialidad.clearValidators();
          especialidad.setValue('');
        }
        especialidad.updateValueAndValidity();
      });
  }

  invalido(nombre: 'nombre' | 'correo' | 'contrasena' | 'especialidad'): boolean {
    const campo = this.formulario.controls[nombre];
    return campo.invalid && (campo.touched || campo.dirty);
  }

  registrar(): void {
    if (this.enviando()) return;

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const v = this.formulario.getRawValue();
    const esVendedor = v.rol === 'VENDEDOR';

    this.enviando.set(true);
    this.errorRegistro.set(null);

    this.auth.registrar({
      nombre: v.nombre,
      correo: v.correo,
      contrasena: v.contrasena,
      rol: esVendedor ? 'VENDEDOR' : 'COMPRADOR',
      especialidad: esVendedor ? v.especialidad : null
    }).subscribe({
      next: () => {
        this.enviando.set(false);
        this.avisos.exito('Cuenta creada. Ahora inicia sesion.');
        this.router.navigate(['/login']);
      },
      error: (error: HttpErrorResponse) => {
        this.enviando.set(false);
        this.errorRegistro.set(traducirError(error));
      }
    });
  }
}