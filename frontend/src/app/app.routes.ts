import { algunRolGuard } from './guards/algun-rol.guard';
import { Reportes } from './components/reportes/reportes';
import { Compatibles } from './components/compatibles/compatibles';
import { VerificarVendedor } from './components/vendedores/verificar-vendedor';
import { Routes } from '@angular/router';
import { Catalogo } from './components/catalogo/catalogo';
import { Login } from './components/login/login';
import { Publicar } from './components/publicar/publicar';
import { Favoritos } from './components/favoritos/favoritos';
import { Registro } from './components/registro/registro';
import { MisCompras } from './components/compras/mis-compras';
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
  {
    path: 'mis-compras',
    component: MisCompras,
    canActivate: [autenticadoGuard, rolGuard('COMPRADOR')]
  },
  { path: 'registro', component: Registro, canActivate: [invitadoGuard] },
  {
    path: 'reportes',
    component: Reportes,
    canActivate: [autenticadoGuard, algunRolGuard('VENDEDOR', 'ADMIN')]
  },
  {
    path: 'compatibles',
    component: Compatibles,
    canActivate: [autenticadoGuard]
  },
  {
    path: 'vendedores',
    component: VerificarVendedor,
    canActivate: [autenticadoGuard, rolGuard('ADMIN')]
  },
  { path: '**', redirectTo: 'catalogo' }
];
