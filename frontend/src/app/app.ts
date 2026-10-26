import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { DialogoConfirmacion } from './shared/components/dialogo-confirmacion/dialogo-confirmacion';
import { DialogoRechazo } from './shared/components/dialogo-rechazo/dialogo-rechazo';
import { Navbar } from './shared/components/navbar/navbar';
import { ToastContainer } from './shared/components/toast-container/toast-container';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, ToastContainer, DialogoConfirmacion, DialogoRechazo],
  template: `
    <app-navbar />
    <main>
      <router-outlet />
    </main>
    <app-toast-container />
    <app-dialogo-confirmacion />
    <app-dialogo-rechazo />
  `,
})
export class App {}
