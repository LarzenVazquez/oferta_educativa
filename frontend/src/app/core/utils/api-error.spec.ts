import { HttpErrorResponse } from '@angular/common/http';
import { aApiError } from './api-error';

describe('aApiError', () => {
  it('sin conexión devuelve el mensaje de servidor no disponible', () => {
    const err = aApiError(new HttpErrorResponse({ status: 0 }));
    expect(err.status).toBe(0);
    expect(err.mensaje).toBe('No se pudo conectar con el servidor');
  });

  it('400 de validación devuelve los errores por campo', () => {
    const err = aApiError(
      new HttpErrorResponse({ status: 400, error: { nombre: 'El nombre es obligatorio' } }),
    );
    expect(err.mensaje).toBe('');
    expect(err.errores['nombre']).toBe('El nombre es obligatorio');
  });

  it('409 devuelve el mensaje de la regla de negocio', () => {
    const err = aApiError(
      new HttpErrorResponse({ status: 409, error: { mensaje: 'Ya existe una división con ese nombre' } }),
    );
    expect(err.status).toBe(409);
    expect(err.mensaje).toBe('Ya existe una división con ese nombre');
  });
});
