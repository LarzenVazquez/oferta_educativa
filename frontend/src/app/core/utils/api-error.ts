import { HttpErrorResponse } from '@angular/common/http';
import { ApiError } from '../models/api.model';

// Convierte la respuesta de error del backend a un formato único.
// Mismo criterio que request() de api.js: 400 => { campo: mensaje }, otros => { mensaje }
export function aApiError(err: unknown): ApiError {
  if (!(err instanceof HttpErrorResponse) || err.status === 0) {
    return { status: 0, mensaje: 'No se pudo conectar con el servidor', errores: {} };
  }

  const cuerpo = (err.error ?? {}) as Record<string, unknown>;
  const mensaje = typeof cuerpo['mensaje'] === 'string' ? (cuerpo['mensaje'] as string) : '';

  if (err.status === 400 && !mensaje) {
    const errores: Record<string, string> = {};
    Object.entries(cuerpo).forEach(([campo, msg]) => (errores[campo] = String(msg)));
    return { status: 400, mensaje: '', errores };
  }

  return { status: err.status, mensaje: mensaje || 'Ocurrió un error', errores: {} };
}
