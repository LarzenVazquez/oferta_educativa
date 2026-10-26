// dto/OperacionResponse<T>: respuesta de registrar, editar, enviar, aprobar, rechazar...
export interface OperacionResponse<T> {
  mensaje: string;
  datos: T;
}

// dto/ErrorResponse y errores de validación del GlobalExceptionHandler
//  - 400 por validación: { campo: mensaje }
//  - cualquier otro error: { mensaje }
export interface ApiError {
  status: number;
  mensaje: string;
  errores: Record<string, string>;
}
