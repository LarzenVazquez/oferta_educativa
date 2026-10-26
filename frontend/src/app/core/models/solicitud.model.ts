import { EstadoAprobacion, TipoRegistro } from './enums.model';

// dto/SolicitudResponse: bandeja del Secretario Académico (HU10)
export interface SolicitudResponse {
  tipo: TipoRegistro;
  id: number;
  nombre: string;
  descripcion: string;
  division: string;
  estadoAprobacion: EstadoAprobacion;
  tipoSolicitud: string;
}

// dto/RechazoRequest (HU12)
export interface RechazoRequest {
  motivo: string;
}
