import { Routes } from '@angular/router';

export default [
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.routes'),
  },
] as Routes;
