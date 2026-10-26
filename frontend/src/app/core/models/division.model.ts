import { EstadoAprobacion, Estatus } from './enums.model';

// dto/DivisionRequest (HU01 / HU02)
export interface DivisionRequest {
  nombre: string;
  descripcion: string;
  estatus: Estatus;
}

// dto/DivisionResponse
export interface DivisionResponse {
  id: number;
  nombre: string;
  descripcion: string;
  estatus: Estatus;
  estadoAprobacion: EstadoAprobacion;
  publicado: boolean;
  motivoRechazo: string | null;
}
