import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { OperacionResponse } from '../models/api.model';
import { TipoRegistro } from '../models/enums.model';
import { SolicitudResponse } from '../models/solicitud.model';

// Consume controller/SecretarioController (/api/admin/secretario)
@Injectable({ providedIn: 'root' })
export class SecretarioService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/admin/secretario`;

  // HU10
  listarSolicitudes(): Observable<SolicitudResponse[]> {
    return this.http.get<SolicitudResponse[]>(`${this.url}/solicitudes`);
  }

  // HU11
  aprobar(tipo: TipoRegistro, id: number): Observable<OperacionResponse<unknown>> {
    const recurso = tipo === 'DIVISION' ? 'divisiones' : 'carreras';
    return this.http.patch<OperacionResponse<unknown>>(`${this.url}/${recurso}/${id}/aprobar`, null);
  }
}
