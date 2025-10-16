import { Routes } from '@angular/router';

export default [
  {
    path: '',
    loadComponent: () => import('./kyc'),
  },
] as Routes;
