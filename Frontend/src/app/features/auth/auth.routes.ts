import { Routes } from '@angular/router';

export default [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },
  {
    path: 'register',
    loadComponent: () => import('./register/register'),
    data: { preload: true },
    title: 'Registro',
  },
  {
    path: 'login',
    loadComponent: () => import('./login/login'),
    data: { preload: true },
    title: 'Iniciar Sesión',
  },
] as Routes;
