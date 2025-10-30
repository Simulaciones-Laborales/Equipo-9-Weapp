import { Routes } from '@angular/router';
import CreditApplications from './credit-applications';

export default [
  {
    path: '',
    component: CreditApplications,
  },
  {
    path: 'nueva-solicitud',
    loadComponent: () => import('./new-credit-application/new-credit-application'),
    data: { preload: true },
  },
  {
    path: ':creditId/historial',
    loadComponent: () => import('./history/history'),
    data: { preload: true },
  },
] as Routes;
