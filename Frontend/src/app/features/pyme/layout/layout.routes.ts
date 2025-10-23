import { Routes } from '@angular/router';
import Layout from './layout';

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
