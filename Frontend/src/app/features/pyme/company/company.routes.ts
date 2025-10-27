import { Routes } from '@angular/router';
import Company from './company';

export default [
  {
    path: '',
    component: Company,
  },
  {
    path: ':id/solicitudes-de-credito',
    loadComponent: () => import('./credits/credits'),
  },
] as Routes;
