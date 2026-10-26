import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { OperacionResponse } from '../models/api.model';
import { DivisionRequest, DivisionResponse } from '../models/division.model';

// Consume controller/DivisionController (/api/admin/divisiones)
@Injectable({ providedIn: 'root' })
export class DivisionService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/admin/divisiones`;

  listar(): Observable<DivisionResponse[]> {
    return this.http.get<DivisionResponse[]>(this.url);
  }

  obtener(id: number): Observable<DivisionResponse> {
    return this.http.get<DivisionResponse>(`${this.url}/${id}`);
  }

  // HU01
  registrar(request: DivisionRequest): Observable<OperacionResponse<DivisionResponse>> {
    return this.http.post<OperacionResponse<DivisionResponse>>(this.url, request);
  }

  // HU02
  editar(id: number, request: DivisionRequest): Observable<OperacionResponse<DivisionResponse>> {
    return this.http.put<OperacionResponse<DivisionResponse>>(`${this.url}/${id}`, request);
  }

  // HU08
  enviarAAprobacion(id: number): Observable<OperacionResponse<DivisionResponse>> {
    return this.http.patch<OperacionResponse<DivisionResponse>>(`${this.url}/${id}/enviar-aprobacion`, null);
  }

  // HU03
  solicitarDesactivacion(id: number): Observable<OperacionResponse<DivisionResponse>> {
    return this.http.patch<OperacionResponse<DivisionResponse>>(
      `${this.url}/${id}/solicitar-desactivacion`,
      null,
    );
  }

  // HU12
  rechazar(id: number, motivo: string): Observable<OperacionResponse<DivisionResponse>> {
    return this.http.patch<OperacionResponse<DivisionResponse>>(`${this.url}/${id}/rechazar`, { motivo });
  }
}
