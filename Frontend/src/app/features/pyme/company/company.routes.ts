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
        title: 'Empresas',
      },
      {
        path: 'registro-empresa',
        loadComponent: () => import('./pages/new-company/new-company'),
        data: { preload: true },
        title: 'Registro de Empresa',
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
        title: 'Verificación de Usuario',
      },
    ],
  },
] as Routes;
