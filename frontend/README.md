# Oferta Educativa — Frontend (Angular)

Frontend en **Angular 22** del microservicio de Oferta Educativa. Reemplaza las páginas HTML + JavaScript
que estaban en `src/main/resources/static` y consume la misma API REST.

## Requisitos

- Node.js **22.22.3 o superior** (o 24.15+) — `node -v`
- El backend corriendo en `http://localhost:8080` (`./mvnw spring-boot:run` en la raíz del proyecto)

## Ejecutar

```bash
cd frontend
npm install      # solo la primera vez
npm start        # http://localhost:4200
```

Otros comandos: `npm run build` (compila a `dist/`) y `npm test` (pruebas unitarias con Vitest).

La URL de la API está en `src/environments/environment.ts`.

## Pantallas

| Ruta | Rol | Historias |
|---|---|---|
| `/admin/oferta` | Administrador | HU03, HU06, HU07, HU08, HU09 |
| `/admin/divisiones/nueva`, `/admin/divisiones/:id/editar` | Administrador | HU01, HU02 |
| `/admin/carreras/nueva`, `/admin/carreras/:id/editar` | Administrador | HU04, HU05 |
| `/secretario` | Secretario Académico | HU10, HU11, HU12, HU13 |
| `/oferta-publica` | Aspirante | HU14 |
| `/oferta-publica/carrera/:id` | Aspirante | HU15 |

## Estructura

Sigue la misma organización por capas del backend:

```
src/app
├── core/
│   ├── models/      Interfaces equivalentes a los dto/ y enums de entity/ del backend
│   ├── services/    Un servicio por controller (DivisionService, CarreraService, OfertaService,
│   │                SecretarioService) + ToastService y DialogoService para la interfaz
│   └── utils/       Etiquetas de los enums y manejo uniforme de errores de la API
├── shared/
│   └── components/  Navbar, EstadoBadge, ToastContainer, DialogoConfirmacion, DialogoRechazo
└── pages/
    ├── admin/       oferta-lista, division-form, carrera-form
    ├── secretario/  panel-secretario
    └── publico/     oferta-publica, carrera-detalle
```

## Equivalencias con el frontend anterior

| Antes (`static/`) | Ahora (Angular) |
|---|---|
| `index.html` | `pages/admin/oferta-lista` |
| `division-form.html` | `pages/admin/division-form` |
| `carrera-form.html` | `pages/admin/carrera-form` |
| `panel-secretario.html` | `pages/secretario/panel-secretario` |
| `js/api.js` → `Api` | `core/services/*.service.ts` |
| `js/api.js` → `toast()`, `confirmar()`, `rechazarSolicitud()` | `ToastService`, `DialogoService` y sus componentes |
| `css/styles.css` | `src/styles.css` (misma paleta y clases) |
