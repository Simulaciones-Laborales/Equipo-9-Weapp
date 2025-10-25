import { Routes } from '@angular/router';

export default [
  {
    path: '',
    loadComponent: () => import('./company'),
  },
  {
    path: ':id/credit-applications',
    loadComponent: () => import('./credits/credits'),
  },
] as Routes;
