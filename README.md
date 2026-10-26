# Oferta Educativa — Microservicio

Microservicio Spring Boot para administrar la oferta académica (divisiones y carreras) con un flujo de
aprobación entre el **Administrador** y el **Secretario Académico**. Solo lo aprobado se publica en la
página web.

## Tecnologías

- Java 21, Spring Boot 4 (Web MVC, Data JPA, Validation)
- H2 en memoria
- Frontend: Angular 22 en `frontend/` (ver `frontend/README.md`)

## Ejecutar

```bash
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

- API: http://localhost:8080
- Frontend: `cd frontend`, `npm install` y `npm start` -> http://localhost:4200
- Consola H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:oferta_educativa`, usuario `sa`, sin contraseña)

## Estructura del backend

```
src/main/java/com/oferta_academica/of_academica
├── config/        CorsConfig
├── controller/    Endpoints REST (reciben la petición y llaman al servicio)
├── dto/           Objetos de entrada (*Request) y salida (*Response)
├── entity/        Entidades JPA y enums. RegistroAprobable contiene el flujo de aprobación
├── exception/     Excepciones de negocio y manejador global de errores
├── repository/    Interfaces Spring Data JPA
└── service/       Lógica de negocio y transacciones
```

## Flujo de aprobación

| Estado | Significado | Visible en la web |
|---|---|---|
| BORRADOR | Registrado o editado, sin enviar | No |
| PENDIENTE | Enviado al Secretario para publicarse | No |
| APROBADO | Aprobado y publicado | Sí |
| RECHAZADO | El Secretario lo rechazó (con motivo) | Solo si se rechazó una desactivación de algo publicado |
| PENDIENTE_DESACTIVACION | Se pidió la baja, en revisión | Sí, si estaba publicado |
| INACTIVO | Baja aprobada | No |

- Mientras hay una solicitud en revisión, el registro no se puede editar.
- Editar un registro lo regresa a BORRADOR.

## Endpoints

Base: `http://localhost:8080`

### Divisiones (Administrador)

| HU | Método | Ruta | Body |
|---|---|---|---|
| — | GET | `/api/admin/divisiones` | |
| — | GET | `/api/admin/divisiones/{id}` | |
| HU01 | POST | `/api/admin/divisiones` | `{ "nombre", "descripcion", "estatus" }` |
| HU02 | PUT | `/api/admin/divisiones/{id}` | `{ "nombre", "descripcion", "estatus" }` |
| HU03 | PATCH | `/api/admin/divisiones/{id}/solicitar-desactivacion` | |
| HU08 | PATCH | `/api/admin/divisiones/{id}/enviar-aprobacion` | |
| HU12 | PATCH | `/api/admin/divisiones/{id}/rechazar` | `{ "motivo" }` |

`estatus`: `ACTIVO` o `INACTIVO`.

### Carreras (Administrador)

| HU | Método | Ruta | Body |
|---|---|---|---|
| — | GET | `/api/admin/carreras` | |
| — | GET | `/api/admin/carreras/{id}` | |
| HU04 | POST | `/api/admin/carreras` | `{ "nombre", "descripcion", "duracionCuatrimestres", "modalidad", "divisionId" }` |
| HU05 | PUT | `/api/admin/carreras/{id}` | igual que POST |
| HU06 | PATCH | `/api/admin/carreras/{id}/solicitar-desactivacion` | |
| HU08 | PATCH | `/api/admin/carreras/{id}/enviar-aprobacion` | |
| HU12 | PATCH | `/api/admin/carreras/{id}/rechazar` | `{ "motivo" }` |
| HU13 | PATCH | `/api/admin/carreras/{id}/despublicar` | |

`modalidad`: `ESCOLARIZADA`, `MIXTA` o `EN_LINEA`.

### Oferta (Administrador)

| HU | Método | Ruta |
|---|---|---|
| HU07 / HU09 | GET | `/api/admin/oferta?nombre=&divisionId=` (filtros opcionales) |

### Secretario Académico

| HU | Método | Ruta |
|---|---|---|
| HU10 | GET | `/api/admin/secretario/solicitudes` |
| HU11 | PATCH | `/api/admin/secretario/divisiones/{id}/aprobar` |
| HU11 | PATCH | `/api/admin/secretario/carreras/{id}/aprobar` |

### Página web (Aspirante)

| HU | Método | Ruta |
|---|---|---|
| HU14 | GET | `/api/public/oferta` |
| HU15 | GET | `/api/public/oferta/carrera/{id}` |

## Respuestas

- Acciones (crear, editar, enviar, aprobar, rechazar): `{ "mensaje": "...", "datos": { ...registro } }`
- Errores de validación (400): `{ "campo": "mensaje", ... }`
- Otros errores: `{ "mensaje": "..." }` con 400 (petición inválida), 404 (no existe) o 409 (no permitido por su estado o nombre duplicado)
