import { Routes } from '@angular/router';
import Clients from './clients';

export default [
  {
    path: '',
    component: Clients,
  },
  {
    path: ':id',
    loadComponent: () => import('./details/details'),
    data: { preload: true },
  },
] as Routes;
