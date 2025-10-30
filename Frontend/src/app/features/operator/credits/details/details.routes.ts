import { Routes } from '@angular/router';
import Details from './details';

export default [
  {
    path: '',
    component: Details,
    title: 'Crédito Detalles',
  },
  {
    path: 'historial',
    loadComponent: () => import('./history/history'),
    title: 'Historial',
  },
] as Routes;
