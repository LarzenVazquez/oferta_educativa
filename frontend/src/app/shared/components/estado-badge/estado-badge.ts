import { Component, computed, input } from '@angular/core';
import { ESTADO_LABEL } from '../../../core/utils/etiquetas';

// Etiqueta de color para Estatus y EstadoAprobacion (antes badge() en api.js)
@Component({
  selector: 'app-estado-badge',
  template: `<span class="badge b-{{ valor() }}">{{ texto() }}</span>`,
})
export class EstadoBadge {
  readonly valor = input.required<string>();
  protected readonly texto = computed(() => ESTADO_LABEL[this.valor()] ?? this.valor());
}
