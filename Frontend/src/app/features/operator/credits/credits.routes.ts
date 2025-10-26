import { Routes } from '@angular/router';
import Credits from './credits';

export default [
  {
    path: '',
    component: Credits,
  },
  {
    path: ':id',
    loadComponent: () => import('./details/details'),
  },
] as Routes;
