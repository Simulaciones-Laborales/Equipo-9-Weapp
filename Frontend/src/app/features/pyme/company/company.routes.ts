import { Routes } from '@angular/router';

export default [
  {
    path: '',
    loadComponent: () => import('./company'),
  },
  {
    path: ':id/solicitudes-de-credito',
    loadComponent: () => import('./credits/credits'),
  },
] as Routes;
