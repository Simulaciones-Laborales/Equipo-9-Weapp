import { Routes } from '@angular/router';
import { loggedGuard } from '@core/guards/logged-guard';

export default [
  {
    path: '',
    loadChildren: () => import('./layout/layout.routes'),
    canActivate: [loggedGuard],
  },
] as Routes;
