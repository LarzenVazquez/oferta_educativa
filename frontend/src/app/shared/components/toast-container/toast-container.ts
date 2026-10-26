import { Component, inject } from '@angular/core';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-toast-container',
  template: `
    <div class="toast-box" aria-live="polite">
      @for (t of toastService.toasts(); track t.id) {
        <div class="toast" [class.err]="t.tipo === 'err'" role="status" (click)="toastService.cerrar(t.id)">
          {{ t.texto }}
        </div>
      }
    </div>
  `,
})
export class ToastContainer {
  protected readonly toastService = inject(ToastService);
}
