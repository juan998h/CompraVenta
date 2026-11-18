import { Routes } from '@angular/router';
import { Catalogo } from './components/catalogo/catalogo';
import { Login } from './components/login/login';
import { autenticadoGuard } from './guards/autenticado.guard';
import { invitadoGuard } from './guards/invitado.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'catalogo', pathMatch: 'full' },
  { path: 'login', component: Login, canActivate: [invitadoGuard] },
  { path: 'catalogo', component: Catalogo, canActivate: [autenticadoGuard] },
  { path: '**', redirectTo: 'catalogo' }
];
