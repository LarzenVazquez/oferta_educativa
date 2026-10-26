import { Injectable, signal } from '@angular/core';

export interface Toast {
  id: number;
  texto: string;
  tipo: 'ok' | 'err';
}

// Avisos flotantes (antes toast() en api.js). Se ocultan solos a los 3.5 s
@Injectable({ providedIn: 'root' })
export class ToastService {
  private siguienteId = 1;
  readonly toasts = signal<Toast[]>([]);

  mostrar(texto: string, tipo: 'ok' | 'err' = 'ok'): void {
    const toast: Toast = { id: this.siguienteId++, texto, tipo };
    this.toasts.update((lista) => [...lista, toast]);
    setTimeout(() => this.cerrar(toast.id), 3500);
  }

  error(texto: string): void {
    this.mostrar(texto, 'err');
  }

  cerrar(id: number): void {
    this.toasts.update((lista) => lista.filter((t) => t.id !== id));
  }
}
