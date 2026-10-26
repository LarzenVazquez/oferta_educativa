import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { OperacionResponse } from '../models/api.model';
import { CarreraRequest, CarreraResponse } from '../models/carrera.model';

// Consume controller/CarreraController (/api/admin/carreras)
@Injectable({ providedIn: 'root' })
export class CarreraService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/admin/carreras`;

  listar(): Observable<CarreraResponse[]> {
    return this.http.get<CarreraResponse[]>(this.url);
  }

  obtener(id: number): Observable<CarreraResponse> {
    return this.http.get<CarreraResponse>(`${this.url}/${id}`);
  }

  // HU04
  registrar(request: CarreraRequest): Observable<OperacionResponse<CarreraResponse>> {
    return this.http.post<OperacionResponse<CarreraResponse>>(this.url, request);
  }

  // HU05
  editar(id: number, request: CarreraRequest): Observable<OperacionResponse<CarreraResponse>> {
    return this.http.put<OperacionResponse<CarreraResponse>>(`${this.url}/${id}`, request);
  }

  // HU08
  enviarAAprobacion(id: number): Observable<OperacionResponse<CarreraResponse>> {
    return this.http.patch<OperacionResponse<CarreraResponse>>(`${this.url}/${id}/enviar-aprobacion`, null);
  }

  // HU06
  solicitarDesactivacion(id: number): Observable<OperacionResponse<CarreraResponse>> {
    return this.http.patch<OperacionResponse<CarreraResponse>>(
      `${this.url}/${id}/solicitar-desactivacion`,
      null,
    );
  }

  // HU13
  despublicar(id: number): Observable<OperacionResponse<CarreraResponse>> {
    return this.http.patch<OperacionResponse<CarreraResponse>>(`${this.url}/${id}/despublicar`, null);
  }

  // HU12
  rechazar(id: number, motivo: string): Observable<OperacionResponse<CarreraResponse>> {
    return this.http.patch<OperacionResponse<CarreraResponse>>(`${this.url}/${id}/rechazar`, { motivo });
  }
}
