import { inject, Injectable } from '@angular/core';
import { TokenStorage } from './token-storage';
import { UserRole } from '@core/models/user-model';

@Injectable({
  providedIn: 'root',
})
export class RouteByRoleService {
  private readonly _tokenStorage = inject(TokenStorage);

  getRoute() {
    const user = this._tokenStorage.user()!;

    if (user.role === UserRole.OPERADOR) {
      return '/operador';
    }

    return '/pyme';
  }
}
