import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AvisoService } from '../../services/aviso.service';
import { SesionService } from '../../services/sesion.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  template: `
    <div class="row justify-content-center">
      <div class="col-12 col-md-6 col-lg-4">
        <h2 class="mb-4 text-center">Iniciar sesion</h2>

        <form [formGroup]="formulario" (ngSubmit)="iniciarSesion()" novalidate>
          <div class="mb-3">
            <label for="username" class="form-label">Correo</label>
            <input
              id="username"
              type="email"
              class="form-control"
              formControlName="username"
              [class.is-invalid]="campoInvalido('username')"
            />
            @if (campoInvalido('username')) {
              <div class="invalid-feedback">Escribe un correo valido.</div>
            }
          </div>

          <div class="mb-3">
            <label for="password" class="form-label">Contrasena</label>
            <input
              id="password"
              type="password"
              class="form-control"
              formControlName="password"
              [class.is-invalid]="campoInvalido('password')"
            />
            @if (campoInvalido('password')) {
              <div class="invalid-feedback">La contrasena debe tener al menos 8 caracteres.</div>
            }
          </div>

          @if (errorLogin()) {
            <div class="alert alert-danger" role="alert">{{ errorLogin() }}</div>
          }

          <button type="submit" class="btn btn-primary w-100" [disabled]="enviando()">
            {{ enviando() ? 'Entrando...' : 'Entrar' }}
          </button>
        </form>
      </div>
    </div>
  `
})
export class Login {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private sesion = inject(SesionService);
  private avisos = inject(AvisoService);
  private router = inject(Router);

  enviando = signal(false);
  errorLogin = signal<string | null>(null);

  formulario = this.fb.nonNullable.group({
    username: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  campoInvalido(nombre: 'username' | 'password'): boolean {
    const campo = this.formulario.controls[nombre];
    return campo.invalid && (campo.touched || campo.dirty);
  }

  iniciarSesion(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.errorLogin.set(null);

    this.auth.login(this.formulario.getRawValue()).subscribe({
      next: (respuesta) => {
        this.sesion.guardarSesion(respuesta);
        this.avisos.exito('Sesion iniciada correctamente.');
        this.router.navigate(['/catalogo']);
      },
      error: (error: HttpErrorResponse) => {
        this.enviando.set(false);
        if (error.status === 0) {
          this.errorLogin.set('No hay conexion con el servidor.');
        } else if (error.status === 400) {
          this.errorLogin.set('Revisa los datos que escribiste.');
        } else if (error.status === 401 || error.status === 403) {
          this.errorLogin.set('Correo o contrasena incorrectos.');
        } else {
          this.errorLogin.set(error.error?.mensaje ?? 'No se pudo iniciar sesion.');
        }
      }
    });
  }
}
