import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { UserRole } from '@core/models/user-model';
import { TokenStorage } from '@core/services/token-storage';
import { key } from '@core/utils/http-utils';
import { MessageService } from 'primeng/api';

export const loggedGuard: CanActivateFn = () => {
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

    router.createUrlTree(['pyme', 'auth', 'login']);
    return false;
  }

  const user = tokenStorage.user();

  if (!user) {
    return false;
  }

  const route = user.role === UserRole.OPERADOR ? 'operador' : 'pyme';
  router.createUrlTree([`${route}`]);

  return true;
};
