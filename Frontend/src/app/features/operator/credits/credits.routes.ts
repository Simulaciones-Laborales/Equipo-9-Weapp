import { Routes } from '@angular/router';
import Credits from './credits';

export default [
  {
    path: '',
    component: Credits,
  },
  {
    path: ':id',
    loadChildren: () => import('./details/details.routes'),
    data: { preload: true },
  },
] as Routes;
