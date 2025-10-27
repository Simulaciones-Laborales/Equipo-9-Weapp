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
      },
      {
        path: 'empresas',
        loadChildren: () => import('./company/company.routes'),
      },
      {
        path: 'perfil',
        loadComponent: () => import('./profile/profile'),
      },
    ],
  },
] as Routes;
