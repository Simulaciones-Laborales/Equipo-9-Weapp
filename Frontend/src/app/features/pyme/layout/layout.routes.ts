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
    ],
  },
] as Routes;
