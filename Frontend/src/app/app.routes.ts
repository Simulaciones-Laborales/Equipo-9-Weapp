import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'pyme',
    loadChildren: () => import('./features/pyme/pyme.routes'),
  },
  {
    path: 'operador',
    loadChildren: () => import('./features/operator/operator.routes'),
  },
  {
    path: '',
    loadComponent: () => import('./features/home/pages/home/home.component')
  }
];
