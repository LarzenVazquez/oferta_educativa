// Textos para mostrar los valores de los enums (antes ESTADO_LABEL y MODALIDAD_LABEL en api.js)

export const ESTADO_LABEL: Record<string, string> = {
  BORRADOR: 'Borrador',
  PENDIENTE: 'Pendiente',
  PENDIENTE_DESACTIVACION: 'Pend. desactivación',
  APROBADO: 'Aprobado',
  RECHAZADO: 'Rechazado',
  INACTIVO: 'Inactivo',
  ACTIVO: 'Activo',
};

export const MODALIDAD_LABEL: Record<string, string> = {
  ESCOLARIZADA: 'Escolarizada',
  MIXTA: 'Mixta',
  EN_LINEA: 'En línea',
};

export const TIPO_LABEL: Record<string, string> = {
  DIVISION: 'División',
  CARRERA: 'Carrera',
};

// Estados en los que el Secretario está revisando y no se permite modificar
export const ESTADOS_EN_REVISION = ['PENDIENTE', 'PENDIENTE_DESACTIVACION'];
