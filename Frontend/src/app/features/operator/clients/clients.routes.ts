import { Routes } from '@angular/router';
import Clients from './clients';

export default [
  {
    path: '',
    component: Clients,
    title: 'Clientes',
  },
  {
    path: ':id',
    loadComponent: () => import('./details/details'),
    data: { preload: true },
    title: 'Cliente Detalles',
  },
] as Routes;
