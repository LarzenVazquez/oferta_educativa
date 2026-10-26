import { Component, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { DialogoRechazo as Rechazo, DialogoService } from '../../../core/services/dialogo.service';
import { CarreraService } from '../../../core/services/carrera.service';
import { DivisionService } from '../../../core/services/division.service';
import { ToastService } from '../../../core/services/toast.service';
import { OperacionResponse } from '../../../core/models/api.model';
import { aApiError } from '../../../core/utils/api-error';

// HU12: el Secretario rechaza una solicitud indicando un motivo obligatorio
@Component({
  selector: 'app-dialogo-rechazo',
  imports: [FormsModule],
  template: `
    @if (dialogoService.rechazo(); as d) {
      <div class="modal-bg" (click)="cerrar(d, false)">
        <form class="modal" role="dialog" aria-modal="true" novalidate
              (click)="$event.stopPropagation()" (ngSubmit)="rechazar(d)">
          <p>Rechazar la solicitud de <b>{{ d.nombre }}</b></p>
          <div class="field">
            <label for="motivo">Motivo del rechazo <span class="req">*</span></label>
            <textarea id="motivo" name="motivo" maxlength="500" [class.invalid]="!!error()"
                      [(ngModel)]="motivo" autofocus></textarea>
            <div class="error">{{ error() }}</div>
            <div class="contador">{{ motivo.length }} / 500</div>
          </div>
          <div class="form-actions">
            <button type="button" class="btn secondary" (click)="cerrar(d, false)">Cancelar</button>
            <button type="submit" class="btn danger" [disabled]="enviando()">Rechazar</button>
          </div>
        </form>
      </div>
    }
  `,
})
export class DialogoRechazo {
  protected readonly dialogoService = inject(DialogoService);
  private readonly divisionService = inject(DivisionService);
  private readonly carreraService = inject(CarreraService);
  private readonly toast = inject(ToastService);

  protected motivo = '';
  protected readonly error = signal('');
  protected readonly enviando = signal(false);

  constructor() {
    // Limpia el formulario cada vez que se abre
    effect(() => {
      if (this.dialogoService.rechazo()) {
        this.motivo = '';
        this.error.set('');
        this.enviando.set(false);
      }
    });
  }

  protected rechazar(d: Rechazo): void {
    this.error.set('');
    const motivo = this.motivo.trim();
    if (!motivo) {
      this.error.set('El motivo de rechazo es obligatorio');
      return;
    }

    const peticion: Observable<OperacionResponse<unknown>> =
      d.tipo === 'DIVISION'
        ? this.divisionService.rechazar(d.id, motivo)
        : this.carreraService.rechazar(d.id, motivo);

    this.enviando.set(true);
    peticion.subscribe({
      next: (r) => {
        this.toast.mostrar(r.mensaje);
        this.cerrar(d, true);
      },
      error: (e) => {
        this.enviando.set(false);
        const err = aApiError(e);
        if (err.status === 400 && err.errores['motivo']) {
          this.error.set(err.errores['motivo']);
        } else {
          this.toast.error(err.mensaje || 'Ocurrió un error');
          this.cerrar(d, false);
        }
      },
    });
  }

  protected cerrar(d: Rechazo, rechazado: boolean): void {
    d.responder(rechazado);
  }
}
