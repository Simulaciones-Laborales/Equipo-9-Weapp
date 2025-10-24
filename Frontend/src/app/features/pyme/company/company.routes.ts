import { Routes } from '@angular/router';

export default [
  {
    path: '',
    loadComponent: () => import('./company'),
  },
  {
    path: 'credits',
    loadComponent: () => import('./credits/credits'),
  },
] as Routes;
