import { Routes } from '@angular/router';

export default [
  {
    path: '',
    loadComponent: () => import('./layout'),
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
      {
        path: 'dashboard',
        loadComponent: () => import('../dashboard/dashboard'),
      },
      {
        path: 'creditos',
        loadComponent: () => import('../credits/credits'),
      },
      {
        path: 'empresa',
        loadComponent: () => import('../company/company'),
      },
      {
        path: 'perfil',
        loadComponent: () => import('../profile/profile'),
      },
    ],
  },
] as Routes;
