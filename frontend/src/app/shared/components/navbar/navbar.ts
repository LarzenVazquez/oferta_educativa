import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

// Menú superior (antes renderHeader() en api.js)
@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive],
  template: `
    <header class="top">
      <a class="marca" routerLink="/admin/oferta">
        <span class="logo" aria-hidden="true">OA</span>
        <h1>Oferta Académica</h1>
      </a>
      <nav>
        <a routerLink="/admin/oferta" routerLinkActive="activo">Oferta</a>
        <a routerLink="/admin/divisiones/nueva" routerLinkActive="activo">+ División</a>
        <a routerLink="/admin/carreras/nueva" routerLinkActive="activo">+ Carrera</a>
        <a routerLink="/secretario" routerLinkActive="activo">Panel Secretario</a>
        <a routerLink="/oferta-publica" routerLinkActive="activo" class="publica">Página web</a>
      </nav>
    </header>
  `,
})
export class Navbar {}
