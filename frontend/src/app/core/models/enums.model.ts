// Espejo de los enums del backend (entity/ y dto/TipoRegistro)

export type Estatus = 'ACTIVO' | 'INACTIVO';

export type EstadoAprobacion =
  | 'BORRADOR'
  | 'PENDIENTE'
  | 'PENDIENTE_DESACTIVACION'
  | 'APROBADO'
  | 'RECHAZADO'
  | 'INACTIVO';

export type Modalidad = 'ESCOLARIZADA' | 'MIXTA' | 'EN_LINEA';

export type TipoRegistro = 'DIVISION' | 'CARRERA';
