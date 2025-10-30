import { Routes } from '@angular/router';
import { Layout } from './layout/layout';

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
        loadComponent: () => import('./dashboard/dashboard'),
        data: { preload: true },
      },
      {
        path: 'clientes',
        loadChildren: () => import('./clients/clients.routes'),
        data: { preload: true },
      },
      {
        path: 'solicitudes',
        loadChildren: () => import('./credits/credits.routes'),
        data: { preload: true },
      },
    ],
  },
] as Routes;
