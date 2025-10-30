import { Routes } from '@angular/router';
import Layout from './layout/layout';

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
        title: 'Inicio',
      },
      {
        path: 'empresas',
        loadChildren: () => import('./company/company.routes'),
        data: { preload: true },
      },
      {
        path: 'perfil',
        loadComponent: () => import('./profile/profile'),
        data: { preload: true },
        title: 'Perfil',
      },
    ],
  },
] as Routes;
