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
      },
      {
        path: 'registro-empresa',
        loadComponent: () => import('./pages/new-company/new-company'),
      },
      {
        path: ':id/solicitudes-de-credito',
        loadChildren: () => import('./pages/credit-applications/credit-applications.routes'),
      },
      {
        path: ':id/verificacion',
        loadComponent: () => import('./pages/kyc-verification/kyc-verification'),
      },
    ],
  },
] as Routes;
