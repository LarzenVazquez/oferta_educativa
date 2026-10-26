import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CarreraDetallePublicaResponse,
  OfertaItemResponse,
  OfertaPublicaDivisionResponse,
} from '../models/oferta.model';

// Consume controller/OfertaController (/api/admin/oferta)
// y controller/OfertaPublicaController (/api/public/oferta)
@Injectable({ providedIn: 'root' })
export class OfertaService {
  private readonly http = inject(HttpClient);
  private readonly urlAdmin = `${environment.apiUrl}/admin/oferta`;
  private readonly urlPublica = `${environment.apiUrl}/public/oferta`;

  // HU07 / HU09: filtros opcionales
  listarParaAdministrador(nombre?: string, divisionId?: number | null): Observable<OfertaItemResponse[]> {
    let params = new HttpParams();
    if (nombre) params = params.set('nombre', nombre);
    if (divisionId) params = params.set('divisionId', divisionId);
    return this.http.get<OfertaItemResponse[]>(this.urlAdmin, { params });
  }

  // HU14
  listarPublica(): Observable<OfertaPublicaDivisionResponse[]> {
    return this.http.get<OfertaPublicaDivisionResponse[]>(this.urlPublica);
  }

  // HU15
  detalleCarreraPublica(id: number): Observable<CarreraDetallePublicaResponse> {
    return this.http.get<CarreraDetallePublicaResponse>(`${this.urlPublica}/carrera/${id}`);
  }
}
