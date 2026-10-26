import { Injectable, signal } from '@angular/core';
import { TipoRegistro } from '../models/enums.model';

export interface DialogoConfirmacion {
  texto: string;
  responder: (ok: boolean) => void;
}

export interface DialogoRechazo {
  tipo: TipoRegistro;
  id: number;
  nombre: string;
  responder: (rechazado: boolean) => void;
}

// Ventanas modales (antes confirmar() y rechazarSolicitud() en api.js)
@Injectable({ providedIn: 'root' })
export class DialogoService {
  readonly confirmacion = signal<DialogoConfirmacion | null>(null);
  readonly rechazo = signal<DialogoRechazo | null>(null);

  // Devuelve true si el usuario presiona "Confirmar"
  confirmar(texto: string): Promise<boolean> {
    return new Promise((resolve) => {
      this.confirmacion.set({
        texto,
        responder: (ok) => {
          this.confirmacion.set(null);
          resolve(ok);
        },
      });
    });
  }

  // HU12: pide el motivo (obligatorio) y rechaza. Devuelve true si se rechazó
  rechazarSolicitud(tipo: TipoRegistro, id: number, nombre: string): Promise<boolean> {
    return new Promise((resolve) => {
      this.rechazo.set({
        tipo,
        id,
        nombre,
        responder: (rechazado) => {
          this.rechazo.set(null);
          resolve(rechazado);
        },
      });
    });
  }
}
