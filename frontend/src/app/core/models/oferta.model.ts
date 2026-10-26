import { EstadoAprobacion, Estatus, Modalidad, TipoRegistro } from './enums.model';

// dto/OfertaItemResponse: fila del listado del Administrador (HU07 / HU09)
export interface OfertaItemResponse {
  tipo: TipoRegistro;
  id: number;
  nombre: string;
  division: string;
  estatus: Estatus;
  estadoAprobacion: EstadoAprobacion;
  motivoRechazo: string | null;
}

// dto/OfertaPublicaCarreraResponse
export interface OfertaPublicaCarreraResponse {
  id: number;
  nombre: string;
  descripcion: string;
  duracionCuatrimestres: number;
  modalidad: Modalidad;
}

// dto/OfertaPublicaDivisionResponse (HU14)
export interface OfertaPublicaDivisionResponse {
  id: number;
  nombre: string;
  descripcion: string;
  carreras: OfertaPublicaCarreraResponse[];
}

// dto/CarreraDetallePublicaResponse (HU15)
export interface CarreraDetallePublicaResponse {
  id: number;
  nombre: string;
  descripcion: string;
  duracionCuatrimestres: number;
  modalidad: Modalidad;
  division: { id: number; nombre: string };
}
