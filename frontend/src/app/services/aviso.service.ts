import { Injectable, signal } from '@angular/core';

export type TipoAviso = 'success' | 'danger' | 'info';

export interface Aviso {
  id: number;
  tipo: TipoAviso;
  mensaje: string;
}

@Injectable({ providedIn: 'root' })
export class AvisoService {
  private contador = 0;

  // Lista de avisos que se estan mostrando ahora mismo
  readonly avisos = signal<Aviso[]>([]);

  exito(mensaje: string): void {
    this.mostrar('success', mensaje);
  }

  error(mensaje: string): void {
    this.mostrar('danger', mensaje);
  }

  info(mensaje: string): void {
    this.mostrar('info', mensaje);
  }

  cerrar(id: number): void {
    this.avisos.update((lista) => lista.filter((aviso) => aviso.id !== id));
  }

  private mostrar(tipo: TipoAviso, mensaje: string): void {
    const id = ++this.contador;
    this.avisos.update((lista) => [...lista, { id, tipo, mensaje }]);
    // Se cierra solo a los 5 segundos
    setTimeout(() => this.cerrar(id), 5000);
  }
}
