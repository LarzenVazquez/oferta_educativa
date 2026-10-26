import { Routes } from '@angular/router';

// Rutas equivalentes a las páginas HTML anteriores:
//   index.html            -> /admin/oferta
//   division-form.html    -> /admin/divisiones/nueva y /admin/divisiones/:id/editar
//   carrera-form.html     -> /admin/carreras/nueva y /admin/carreras/:id/editar
//   panel-secretario.html -> /secretario
// Nuevas (HU14 / HU15): /oferta-publica y /oferta-publica/carrera/:id
export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'admin/oferta' },
  {
    path: 'admin/oferta',
    title: 'Oferta académica',
    loadComponent: () => import('./pages/admin/oferta-lista/oferta-lista').then((m) => m.OfertaLista),
  },
  {
    path: 'admin/divisiones/nueva',
    title: 'Registrar división',
    loadComponent: () => import('./pages/admin/division-form/division-form').then((m) => m.DivisionForm),
  },
  {
    path: 'admin/divisiones/:id/editar',
    title: 'Editar división',
    loadComponent: () => import('./pages/admin/division-form/division-form').then((m) => m.DivisionForm),
  },
  {
    path: 'admin/carreras/nueva',
    title: 'Registrar carrera',
    loadComponent: () => import('./pages/admin/carrera-form/carrera-form').then((m) => m.CarreraForm),
  },
  {
    path: 'admin/carreras/:id/editar',
    title: 'Editar carrera',
    loadComponent: () => import('./pages/admin/carrera-form/carrera-form').then((m) => m.CarreraForm),
  },
  {
    path: 'secretario',
    title: 'Panel del Secretario',
    loadComponent: () =>
      import('./pages/secretario/panel-secretario/panel-secretario').then((m) => m.PanelSecretario),
  },
  {
    path: 'oferta-publica',
    title: 'Oferta educativa',
    loadComponent: () =>
      import('./pages/publico/oferta-publica/oferta-publica').then((m) => m.OfertaPublica),
  },
  {
    path: 'oferta-publica/carrera/:id',
    title: 'Detalle de carrera',
    loadComponent: () =>
      import('./pages/publico/carrera-detalle/carrera-detalle').then((m) => m.CarreraDetalle),
  },
  { path: '**', redirectTo: 'admin/oferta' },
];
