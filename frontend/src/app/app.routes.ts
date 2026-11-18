import { Routes } from '@angular/router';
import { Catalogo } from './components/catalogo/catalogo';
import { Login } from './components/login/login';

export const routes: Routes = [
  { path: '', redirectTo: 'catalogo', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'catalogo', component: Catalogo },
  { path: '**', redirectTo: 'catalogo' }
];
