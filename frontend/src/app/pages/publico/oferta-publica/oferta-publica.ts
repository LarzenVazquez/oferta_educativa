import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { OfertaPublicaDivisionResponse } from '../../../core/models/oferta.model';
import { OfertaService } from '../../../core/services/oferta.service';
import { MODALIDAD_LABEL } from '../../../core/utils/etiquetas';

// Rol: Aspirante - HU14: divisiones con sus carreras (solo lo aprobado)
@Component({
  selector: 'app-oferta-publica',
  imports: [RouterLink],
  templateUrl: './oferta-publica.html',
})
export class OfertaPublica implements OnInit {
  private readonly ofertaService = inject(OfertaService);

  protected readonly modalidadLabel = MODALIDAD_LABEL;
  protected readonly divisiones = signal<OfertaPublicaDivisionResponse[]>([]);
  protected readonly cargando = signal(true);
  protected readonly errorCarga = signal(false);
  protected readonly totalCarreras = computed(() =>
    this.divisiones().reduce((total, d) => total + d.carreras.length, 0),
  );

  ngOnInit(): void {
    this.ofertaService.listarPublica().subscribe({
      next: (d) => {
        this.divisiones.set(d);
        this.cargando.set(false);
      },
      error: () => {
        this.errorCarga.set(true);
        this.cargando.set(false);
      },
    });
  }
}
