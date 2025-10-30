import { Routes } from '@angular/router';
import Company from './company';

export default [
  {
    path: '',
    component: Company,
    children: [
      {
        path: '',
        loadComponent: () => import('./pages/list/list'),
        data: { preload: true },
      },
      {
        path: 'registro-empresa',
        loadComponent: () => import('./pages/new-company/new-company'),
        data: { preload: true },
      },
      {
        path: ':id/solicitudes-de-credito',
        loadChildren: () => import('./pages/credit-applications/credit-applications.routes'),
        data: { preload: true },
      },
      {
        path: ':id/verificacion',
        loadComponent: () => import('./pages/kyc-verification/kyc-verification'),
        data: { preload: true },
      },
    ],
  },
] as Routes;
