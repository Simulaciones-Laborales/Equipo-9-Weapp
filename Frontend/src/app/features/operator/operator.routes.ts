import { Routes } from '@angular/router';

export default [
  {
    path: '',
    loadChildren: () => import('./layout/layout.routes'),
  },
] as Routes;
