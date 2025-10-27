import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { loggedGuard } from '@core/guards/logged-guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/home/pages/home/home.component'),
  },
  {
    path: 'auth',
    canActivate: [authGuard],
    loadChildren: () => import('./features/auth/auth.routes'),
  },
  {
    path: 'pyme',
    canActivate: [loggedGuard],
    loadChildren: () => import('./features/pyme/pyme.routes'),
  },
  {
    path: 'operador',
    canActivate: [loggedGuard],
    loadChildren: () => import('./features/operator/operator.routes'),
  },
];
