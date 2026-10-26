import { Component, OnInit, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CarreraRequest, CarreraResponse } from '../../../core/models/carrera.model';
import { DivisionResponse } from '../../../core/models/division.model';
import { CarreraService } from '../../../core/services/carrera.service';
import { DivisionService } from '../../../core/services/division.service';
import { ToastService } from '../../../core/services/toast.service';
import { aApiError } from '../../../core/utils/api-error';
import { ESTADOS_EN_REVISION, MODALIDAD_LABEL } from '../../../core/utils/etiquetas';
import { EstadoBadge } from '../../../shared/components/estado-badge/estado-badge';

// Rol: Administrador - HU04 (registrar carrera) y HU05 (editar carrera)
@Component({
  selector: 'app-carrera-form',
  imports: [FormsModule, RouterLink, EstadoBadge],
  templateUrl: './carrera-form.html',
})
export class CarreraForm implements OnInit {
  private readonly carreraService = inject(CarreraService);
  private readonly divisionService = inject(DivisionService);
  private readonly toast = inject(ToastService);
  private readonly router = inject(Router);

  // Viene de la ruta /admin/carreras/:id/editar
  readonly id = input<string>();

  protected readonly modalidades = Object.entries(MODALIDAD_LABEL);
  protected modelo: CarreraRequest = {
    nombre: '',
    descripcion: '',
    duracionCuatrimestres: null,
    modalidad: null,
    divisionId: null,
  };
  protected readonly divisiones = signal<DivisionResponse[]>([]);
  protected readonly actual = signal<CarreraResponse | null>(null);
  protected readonly errores = signal<Record<string, string>>({});
  protected readonly bloqueado = signal(false);
  protected readonly guardando = signal(false);

  ngOnInit(): void {
    // Solo divisiones que no estén inactivas
    this.divisionService.listar().subscribe({
      next: (lista) => {
        this.divisiones.set(lista.filter((d) => d.estadoAprobacion !== 'INACTIVO'));
        if (this.id()) this.precargar(Number(this.id()));
      },
    });
  }

  private precargar(id: number): void {
    this.carreraService.obtener(id).subscribe({
      next: (c) => {
        this.modelo = {
          nombre: c.nombre,
          descripcion: c.descripcion,
          duracionCuatrimestres: c.duracionCuatrimestres,
          modalidad: c.modalidad,
          divisionId: c.divisionId,
        };
        this.actual.set(c);
        this.bloqueado.set(ESTADOS_EN_REVISION.includes(c.estadoAprobacion));
      },
      error: (e) => this.toast.error(aApiError(e).mensaje || 'Error'),
    });
  }

  protected guardar(): void {
    this.errores.set({});
    const id = this.id() ? Number(this.id()) : null;
    const peticion = id
      ? this.carreraService.editar(id, this.modelo)
      : this.carreraService.registrar(this.modelo);

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
