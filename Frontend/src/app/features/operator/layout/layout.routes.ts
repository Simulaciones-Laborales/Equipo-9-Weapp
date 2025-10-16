import { Routes } from '@angular/router';
import { Layout } from './layout';

export default [
  {
    path: '',
    component: Layout,
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
        path: 'clientes',
        loadComponent: () => import('../clients/clients'),
      },
      {
        path: 'solicitudes',
        loadComponent: () => import('../credits/credits'),
      },
    ],
  },
] as Routes;
