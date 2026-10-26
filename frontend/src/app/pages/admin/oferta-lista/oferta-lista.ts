import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { OperacionResponse } from '../../../core/models/api.model';
import { DivisionResponse } from '../../../core/models/division.model';
import { OfertaItemResponse } from '../../../core/models/oferta.model';
import { CarreraService } from '../../../core/services/carrera.service';
import { DialogoService } from '../../../core/services/dialogo.service';
import { DivisionService } from '../../../core/services/division.service';
import { OfertaService } from '../../../core/services/oferta.service';
import { ToastService } from '../../../core/services/toast.service';
import { aApiError } from '../../../core/utils/api-error';
import { ESTADOS_EN_REVISION, TIPO_LABEL } from '../../../core/utils/etiquetas';
import { EstadoBadge } from '../../../shared/components/estado-badge/estado-badge';

type Accion = 'enviar' | 'desactivar';

// Rol: Administrador - HU07 (listado con búsqueda y filtro), HU09 (estado y motivo de rechazo),
// HU03/HU06 (solicitar desactivación) y HU08 (enviar a aprobación)
@Component({
  selector: 'app-oferta-lista',
  imports: [FormsModule, RouterLink, EstadoBadge],
  templateUrl: './oferta-lista.html',
})
export class OfertaLista implements OnInit {
  private readonly ofertaService = inject(OfertaService);
  private readonly divisionService = inject(DivisionService);
  private readonly carreraService = inject(CarreraService);
  private readonly dialogo = inject(DialogoService);
  private readonly toast = inject(ToastService);

  protected readonly tipoLabel = TIPO_LABEL;
  protected readonly divisiones = signal<DivisionResponse[]>([]);
  protected readonly filas = signal<OfertaItemResponse[]>([]);
  protected readonly cargando = signal(true);
  protected readonly errorCarga = signal(false);

  protected nombre = '';
  protected divisionId: number | null = null;
  private espera?: ReturnType<typeof setTimeout>;

  protected readonly totalDivisiones = computed(() => this.filas().filter((f) => f.tipo === 'DIVISION').length);
  protected readonly totalCarreras = computed(() => this.filas().filter((f) => f.tipo === 'CARRERA').length);

  ngOnInit(): void {
    this.divisionService.listar().subscribe({ next: (d) => this.divisiones.set(d) });
    this.cargarOferta();
  }

  // Búsqueda mientras se escribe (espera 300 ms como antes)
  protected alEscribir(): void {
    clearTimeout(this.espera);
    this.espera = setTimeout(() => this.cargarOferta(), 300);
  }

  protected cargarOferta(): void {
    this.ofertaService.listarParaAdministrador(this.nombre.trim(), this.divisionId).subscribe({
      next: (filas) => {
        this.filas.set(filas);
        this.errorCarga.set(false);
        this.cargando.set(false);
      },
      error: (e) => {
        this.errorCarga.set(true);
        this.cargando.set(false);
        this.toast.error(aApiError(e).mensaje || 'Error');
      },
    });
  }

  // Reglas de los botones (mismas que index.html)
  protected enRevision(f: OfertaItemResponse): boolean {
    return ESTADOS_EN_REVISION.includes(f.estadoAprobacion);
  }
  protected puedeEditar(f: OfertaItemResponse): boolean {
    return !this.enRevision(f);
  }
  protected puedeEnviar(f: OfertaItemResponse): boolean {
    return ['BORRADOR', 'RECHAZADO'].includes(f.estadoAprobacion);
  }
  protected puedeDesactivar(f: OfertaItemResponse): boolean {
    return !this.enRevision(f) && f.estadoAprobacion !== 'INACTIVO';
  }

  protected rutaEditar(f: OfertaItemResponse): string[] {
    return f.tipo === 'DIVISION'
      ? ['/admin/divisiones', String(f.id), 'editar']
      : ['/admin/carreras', String(f.id), 'editar'];
  }

  protected async ejecutar(accion: Accion, f: OfertaItemResponse): Promise<void> {
    const pregunta =
      accion === 'enviar'
        ? `¿Enviar a aprobación "${f.nombre}"?`
        : `¿Solicitar la desactivación de "${f.nombre}"?`;
    if (!(await this.dialogo.confirmar(pregunta))) return;

    let peticion: Observable<OperacionResponse<unknown>>;
    if (f.tipo === 'DIVISION') {
      peticion =
        accion === 'enviar'
          ? this.divisionService.enviarAAprobacion(f.id)
          : this.divisionService.solicitarDesactivacion(f.id);
    } else {
      peticion =
        accion === 'enviar'
          ? this.carreraService.enviarAAprobacion(f.id)
          : this.carreraService.solicitarDesactivacion(f.id);
    }

    peticion.subscribe({
      next: (r) => {
        this.toast.mostrar(r.mensaje);
        this.cargarOferta();
      },
      error: (e) => this.toast.error(aApiError(e).mensaje || 'Error'),
    });
  }
}
