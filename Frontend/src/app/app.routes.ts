import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { loggedGuard } from '@core/guards/logged-guard';
import { UserRole } from '@core/models/user-model';
import HomeComponent from '@features/home/pages/home/home.component';
import NotFound from '@features/not-found/not-found';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
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
    data: { roles: [UserRole.PYME] },
  },
  {
    path: 'operador',
    canActivate: [loggedGuard],
    loadChildren: () => import('./features/operator/operator.routes'),
    data: { roles: [UserRole.OPERADOR] },
  },
  {
    path: '**',
    component: NotFound,
  },
];
