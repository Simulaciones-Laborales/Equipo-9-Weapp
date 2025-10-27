import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { RouteByRoleService } from '@core/services/route-by-role-service';
import { TokenStorage } from '@core/services/token-storage';
import { key } from '@core/utils/http-utils';
import { MessageService } from 'primeng/api';

export const loggedGuard: CanActivateFn = (route, state) => {
  const tokenStorage = inject(TokenStorage);
  const router = inject(Router);

  if (!tokenStorage.token()) {
    const messageService = inject(MessageService);

    messageService.add({
      key,
      severity: 'error',
      summary: 'No estás autenticado',
      detail: 'Por favor, inicia sesión para poder acceder al sistema',
    });

    return router.createUrlTree(['auth', 'login']);
  }

  const path = `/${route.routeConfig?.path}`;
  const navigateTo = inject(RouteByRoleService).getRoute();

  if (!path?.includes(navigateTo)) {
    return router.createUrlTree([navigateTo]);
  }

  return true;
};
