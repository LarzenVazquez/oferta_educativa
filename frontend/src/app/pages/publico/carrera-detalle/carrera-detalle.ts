import { Component, OnInit, inject, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CarreraDetallePublicaResponse } from '../../../core/models/oferta.model';
import { OfertaService } from '../../../core/services/oferta.service';
import { aApiError } from '../../../core/utils/api-error';
import { MODALIDAD_LABEL } from '../../../core/utils/etiquetas';

// Rol: Aspirante - HU15: detalle de una carrera (descripción, duración y modalidad)
@Component({
  selector: 'app-carrera-detalle',
  imports: [RouterLink],
  templateUrl: './carrera-detalle.html',
})
export class CarreraDetalle implements OnInit {
  private readonly ofertaService = inject(OfertaService);

  // Viene de la ruta /oferta-publica/carrera/:id
  readonly id = input.required<string>();

  protected readonly modalidadLabel = MODALIDAD_LABEL;
  protected readonly carrera = signal<CarreraDetallePublicaResponse | null>(null);
  protected readonly error = signal('');

  ngOnInit(): void {
    this.ofertaService.detalleCarreraPublica(Number(this.id())).subscribe({
      next: (c) => this.carrera.set(c),
      error: (e) => this.error.set(aApiError(e).mensaje || 'No se encontró la carrera'),
    });
  }
}
