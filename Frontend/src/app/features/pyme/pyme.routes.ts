import { Routes } from '@angular/router';

export default [
  {
    path: '',
    loadChildren: () => import('./layout/layout.routes'),
  },
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.routes'),
  },
  {
    path: 'kyc',
    loadChildren: () => import('./kyc/kyc.routes'),
  },
] as Routes;
