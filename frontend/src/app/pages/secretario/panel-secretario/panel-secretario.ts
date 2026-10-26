import { Component, OnInit, inject, signal } from '@angular/core';
import { CarreraResponse } from '../../../core/models/carrera.model';
import { SolicitudResponse } from '../../../core/models/solicitud.model';
import { CarreraService } from '../../../core/services/carrera.service';
import { DialogoService } from '../../../core/services/dialogo.service';
import { SecretarioService } from '../../../core/services/secretario.service';
import { ToastService } from '../../../core/services/toast.service';
import { aApiError } from '../../../core/utils/api-error';
import { MODALIDAD_LABEL, TIPO_LABEL } from '../../../core/utils/etiquetas';
import { EstadoBadge } from '../../../shared/components/estado-badge/estado-badge';

// Rol: Secretario Académico - HU10 (solicitudes), HU11 (aprobar), HU12 (rechazar) y HU13 (despublicar)
@Component({
  selector: 'app-panel-secretario',
  imports: [EstadoBadge],
  templateUrl: './panel-secretario.html',
})
export class PanelSecretario implements OnInit {
  private readonly secretarioService = inject(SecretarioService);
  private readonly carreraService = inject(CarreraService);
  private readonly dialogo = inject(DialogoService);
  private readonly toast = inject(ToastService);

  protected readonly tipoLabel = TIPO_LABEL;
  protected readonly modalidadLabel = MODALIDAD_LABEL;
  protected readonly solicitudes = signal<SolicitudResponse[]>([]);
  protected readonly cargando = signal(true);
  protected readonly errorCarga = signal(false);
  protected readonly publicadas = signal<CarreraResponse[]>([]);

  ngOnInit(): void {
    this.cargarSolicitudes();
    this.cargarPublicadas();
  }

  // HU10
  protected cargarSolicitudes(): void {
    this.secretarioService.listarSolicitudes().subscribe({
      next: (s) => {
        this.solicitudes.set(s);
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

  // HU13: carreras aprobadas que se pueden retirar de la web
  private cargarPublicadas(): void {
    this.carreraService.listar().subscribe({
      next: (lista) => this.publicadas.set(lista.filter((c) => c.estadoAprobacion === 'APROBADO')),
    });
  }

  // HU11
  protected async aprobar(s: SolicitudResponse): Promise<void> {
    if (!(await this.dialogo.confirmar('¿Estás seguro de aprobar esta solicitud?'))) return;
    this.secretarioService.aprobar(s.tipo, s.id).subscribe({
      next: (r) => {
        this.toast.mostrar(r.mensaje);
        this.recargar();
      },
      error: (e) => this.toast.error(aApiError(e).mensaje || 'Error al aprobar'),
    });
  }

  // HU12: pide el motivo obligatorio y rechaza
  protected async rechazar(s: SolicitudResponse): Promise<void> {
    if (await this.dialogo.rechazarSolicitud(s.tipo, s.id, s.nombre)) this.recargar();
  }

  // HU13
  protected async despublicar(c: CarreraResponse): Promise<void> {
    if (!(await this.dialogo.confirmar(`¿Despublicar "${c.nombre}"? Dejará de verse en la página web.`))) return;
    this.carreraService.despublicar(c.id).subscribe({
      next: (r) => {
        this.toast.mostrar(r.mensaje);
        this.recargar();
      },
      error: (e) => this.toast.error(aApiError(e).mensaje || 'Error'),
    });
  }

  private recargar(): void {
    this.cargarSolicitudes();
    this.cargarPublicadas();
  }
}
