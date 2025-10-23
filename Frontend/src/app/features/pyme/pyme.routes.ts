import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { loggedGuard } from '@core/guards/logged-guard';

export default [
  {
    path: '',
    loadChildren: () => import('./layout/layout.routes'),
    canActivate: [loggedGuard],
  },
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.routes'),
    canActivate: [authGuard],
  },
] as Routes;
