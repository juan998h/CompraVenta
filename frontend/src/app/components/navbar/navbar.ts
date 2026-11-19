import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { SesionService } from '../../services/sesion.service';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive],
  template: `
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <div class="container">
        <a class="navbar-brand" routerLink="/catalogo">Bikers Dreams</a>

        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#menuPrincipal"
          aria-controls="menuPrincipal"
          aria-expanded="false"
          aria-label="Abrir menu"
        >
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="menuPrincipal">
          <ul class="navbar-nav me-auto">
            @if (sesion.haySesionActiva()) {
              <li class="nav-item">
                <a
                  class="nav-link"
                  routerLink="/catalogo"
                  routerLinkActive="active"
                >Catalogo</a>
              </li>
            }
                      @if (sesion.haySesionActiva() && sesion.tieneRol('VENDEDOR')) {
              <li class="nav-item">
                <a
                  class="nav-link"
                  routerLink="/publicar"
                  routerLinkActive="active"
                >Publicar</a>
              </li>
            }
            @if (sesion.haySesionActiva() && sesion.tieneRol('ADMIN')) {
              <li class="nav-item">
                <a
                  class="nav-link"
                  routerLink="/admin/compras"
                  routerLinkActive="active"
                >Confirmar compras</a>
              </li>
            }
                      @if (sesion.haySesionActiva() && sesion.tieneRol('COMPRADOR')) {
              <li class="nav-item">
                <a
                  class="nav-link"
                  routerLink="/favoritos"
                  routerLinkActive="active"
                >Favoritos</a>
              </li>
            }
          </ul>

          @if (sesion.haySesionActiva()) {
            <span class="navbar-text me-3">
              {{ sesion.roles().join(', ') }}
            </span>
            <button
              type="button"
              class="btn btn-outline-light"
              (click)="sesion.cerrarSesion()"
            >
              Cerrar sesion
            </button>
          } @else {
            <a class="btn btn-outline-light" routerLink="/login">
              Iniciar sesion
            </a>
          }
        </div>
      </div>
    </nav>
  `
})
export class Navbar {
  sesion = inject(SesionService);
}


