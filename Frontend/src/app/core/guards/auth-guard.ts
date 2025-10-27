import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { RouteByRoleService } from '@core/services/route-by-role-service';
import { TokenStorage } from '@core/services/token-storage';

export const authGuard: CanActivateFn = () => {
  const tokenStorage = inject(TokenStorage);

  if (!tokenStorage.token()) {
    return true;
  }

  const router = inject(Router);
  const routeByRoleService = inject(RouteByRoleService);

  return router.createUrlTree([routeByRoleService.getRoute()]);
};
