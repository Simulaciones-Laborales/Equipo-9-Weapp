import { Routes } from '@angular/router';
import Details from './details';

export default [
  {
    path: '',
    component: Details,
  },
  {
    path: 'historial',
    loadComponent: () => import('./history/history'),
  },
] as Routes;
