import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { UserRole } from '@core/models/user-model';
import { TokenStorage } from '@core/services/token-storage';

export const authGuard: CanActivateFn = () => {
  const tokenStorage = inject(TokenStorage);

  if (!tokenStorage.token()) {
    return true;
  }

  const { role } = tokenStorage.user()!;
  const router = inject(Router);

  const route = role === UserRole.OPERADOR ? 'operador' : 'pyme';
  router.navigateByUrl(`/${route}`);

  return false;
};
