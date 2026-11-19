import { Routes } from '@angular/router';
import { Catalogo } from './components/catalogo/catalogo';
import { Login } from './components/login/login';
import { Publicar } from './components/publicar/publicar';
import { Favoritos } from './components/favoritos/favoritos';
import { AdminCompras } from './components/compras/admin-compras';
import { autenticadoGuard } from './guards/autenticado.guard';
import { invitadoGuard } from './guards/invitado.guard';
import { rolGuard } from './guards/rol.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'catalogo', pathMatch: 'full' },
  { path: 'login', component: Login, canActivate: [invitadoGuard] },
  { path: 'catalogo', component: Catalogo, canActivate: [autenticadoGuard] },
  {
    path: 'publicar',
    component: Publicar,
    canActivate: [autenticadoGuard, rolGuard('VENDEDOR')]
  },
  {
    path: 'favoritos',
    component: Favoritos,
    canActivate: [autenticadoGuard, rolGuard('COMPRADOR')]
  },
  {
    path: 'admin/compras',
    component: AdminCompras,
    canActivate: [autenticadoGuard, rolGuard('ADMIN')]
  },
  { path: '**', redirectTo: 'catalogo' }
];
