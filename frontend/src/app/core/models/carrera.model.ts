import { EstadoAprobacion, Estatus, Modalidad } from './enums.model';

// dto/CarreraRequest (HU04 / HU05)
export interface CarreraRequest {
  nombre: string;
  descripcion: string;
  duracionCuatrimestres: number | null;
  modalidad: Modalidad | null;
  divisionId: number | null;
}

// dto/CarreraResponse
export interface CarreraResponse {
  id: number;
  nombre: string;
  descripcion: string;
  duracionCuatrimestres: number;
  modalidad: Modalidad;
  divisionId: number;
  divisionNombre: string;
  estatus: Estatus;
  estadoAprobacion: EstadoAprobacion;
  publicado: boolean;
  motivoRechazo: string | null;
}
