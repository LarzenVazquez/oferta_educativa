import { Component, inject } from '@angular/core';
import { DialogoService } from '../../../core/services/dialogo.service';

@Component({
  selector: 'app-dialogo-confirmacion',
  template: `
    @if (dialogoService.confirmacion(); as d) {
      <div class="modal-bg" (click)="d.responder(false)">
        <div class="modal" role="dialog" aria-modal="true" (click)="$event.stopPropagation()">
          <p>{{ d.texto }}</p>
          <div class="form-actions">
            <button type="button" class="btn secondary" (click)="d.responder(false)">Cancelar</button>
            <button type="button" class="btn danger" (click)="d.responder(true)">Confirmar</button>
          </div>
        </div>
      </div>
    }
  `,
})
export class DialogoConfirmacion {
  protected readonly dialogoService = inject(DialogoService);
}
