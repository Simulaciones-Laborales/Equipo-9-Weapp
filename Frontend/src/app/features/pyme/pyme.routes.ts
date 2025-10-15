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
] as Routes;
