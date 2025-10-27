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
  },
  {
    path: 'login',
    loadComponent: () => import('./login/login'),
  },
] as Routes;
