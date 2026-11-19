import { Component, computed, inject, signal } from '@angular/core';
import { RepuestoMasVendidoResponse } from '../../models/repuesto-mas-vendido-response';
import { ReporteService } from '../../services/reporte.service';

@Component({
  selector: 'app-reportes',
  template: `
    <h2 class="mb-4">Repuestos mas vendidos</h2>

    <div class="d-flex align-items-center gap-2 mb-4">
      <label for="limite" class="form-label mb-0">Mostrar los primeros</label>
      <select #limite id="limite" class="form-select w-auto"
              [disabled]="cargando()"
              (change)="cargar(+limite.value)">
        <option value="3">3</option>
        <option value="5" selected>5</option>
        <option value="10">10</option>
      </select>
    </div>

    @if (cargando()) {
      <p role="status">Cargando reporte...</p>
    } @else if (hayError()) {
      <div class="alert alert-danger">
        No se pudo cargar el reporte.
        <button class="btn btn-sm btn-outline-danger ms-2"
                (click)="cargar(ultimoLimite)">Reintentar</button>
      </div>
    } @else if (lista(); as datos) {
      @if (datos.length === 0) {
        <div class="alert alert-info">Aun no hay ventas completadas.</div>
      } @else {
        <div class="card">
          <ul class="list-group list-group-flush">
            @for (r of datos; track r.repuestoId; let i = $index) {
              <li class="list-group-item bg-transparent text-white">
                <div class="d-flex justify-content-between gap-3">
                  <span>{{ i + 1 }}. {{ r.nombre }}</span>
                  <strong>{{ r.unidadesVendidas }} vendidas</strong>
                </div>
                <div class="progress mt-2" style="height: 8px">
                  <div class="progress-bar"
                       [style.width.%]="(r.unidadesVendidas / maximo()) * 100">
                  </div>
                </div>
              </li>
            }
          </ul>
        </div>
      }
    }
  `
})
export class Reportes {
  private reportes = inject(ReporteService);

  lista = signal<RepuestoMasVendidoResponse[] | null>(null);
  hayError = signal(false);
  cargando = signal(false);
  ultimoLimite = 5;

  maximo = computed(() =>
    Math.max(1, ...(this.lista() ?? []).map((r) => r.unidadesVendidas))
  );

  constructor() {
    this.cargar(5);
  }

  cargar(limite: number): void {
    if (this.cargando()) return;
    this.ultimoLimite = limite;
    this.hayError.set(false);
    this.cargando.set(true);

    this.reportes.masVendidos(limite).subscribe({
      next: (datos) => {
        this.lista.set(datos);
        this.cargando.set(false);
      },
      error: () => {
        this.hayError.set(true);
        this.cargando.set(false);
      }
    });
  }
}