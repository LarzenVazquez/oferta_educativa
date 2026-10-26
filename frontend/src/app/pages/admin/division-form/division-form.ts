import { Component, OnInit, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { DivisionRequest, DivisionResponse } from '../../../core/models/division.model';
import { DivisionService } from '../../../core/services/division.service';
import { ToastService } from '../../../core/services/toast.service';
import { aApiError } from '../../../core/utils/api-error';
import { ESTADOS_EN_REVISION } from '../../../core/utils/etiquetas';
import { EstadoBadge } from '../../../shared/components/estado-badge/estado-badge';

// Rol: Administrador - HU01 (registrar división) y HU02 (editar división)
@Component({
  selector: 'app-division-form',
  imports: [FormsModule, RouterLink, EstadoBadge],
  templateUrl: './division-form.html',
})
export class DivisionForm implements OnInit {
  private readonly divisionService = inject(DivisionService);
  private readonly toast = inject(ToastService);
  private readonly router = inject(Router);

  // Viene de la ruta /admin/divisiones/:id/editar (withComponentInputBinding)
  readonly id = input<string>();

  protected modelo: DivisionRequest = { nombre: '', descripcion: '', estatus: 'ACTIVO' };
  protected readonly actual = signal<DivisionResponse | null>(null);
  protected readonly errores = signal<Record<string, string>>({});
  protected readonly bloqueado = signal(false);
  protected readonly guardando = signal(false);

  ngOnInit(): void {
    if (this.id()) this.precargar(Number(this.id()));
  }

  private precargar(id: number): void {
    this.divisionService.obtener(id).subscribe({
      next: (d) => {
        this.modelo = { nombre: d.nombre, descripcion: d.descripcion, estatus: d.estatus };
        this.actual.set(d);
        this.bloqueado.set(ESTADOS_EN_REVISION.includes(d.estadoAprobacion));
      },
      error: (e) => this.toast.error(aApiError(e).mensaje || 'Error'),
    });
  }

  protected guardar(): void {
    this.errores.set({});
    const id = this.id() ? Number(this.id()) : null;
    const peticion = id
      ? this.divisionService.editar(id, this.modelo)
      : this.divisionService.registrar(this.modelo);

    this.guardando.set(true);
    peticion.subscribe({
      next: (r) => {
        this.toast.mostrar(r.mensaje);
        setTimeout(() => this.router.navigate(['/admin/oferta']), 700);
      },
      error: (e) => {
        this.guardando.set(false);
        const err = aApiError(e);
        if (err.status === 400 && !err.mensaje) this.errores.set(err.errores);
        else this.toast.error(err.mensaje || 'Ocurrió un error');
      },
    });
  }
}
