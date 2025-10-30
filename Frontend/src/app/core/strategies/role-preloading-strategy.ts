import { inject, Injectable } from '@angular/core';
import { PreloadingStrategy, Route } from '@angular/router';
import { TokenStorage } from '@core/services/token-storage';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RolePreloadingStrategy implements PreloadingStrategy {
  private readonly _tokenStorage = inject(TokenStorage);

  preload(route: Route, fn: () => Observable<any>): Observable<any> {
    const user = this._tokenStorage.user();

    if (!user) {
      if (route.path === 'auth' || route.path === '') {
        return fn();
      }

      return of(null);
    }

    const data = route.data;
    const roles = data?.['roles'];
    const preload = data?.['preload'];

    if (!roles && preload) {
      return fn();
    }

    const { role } = user;

    if (data && roles && roles.includes(role)) {
      route.data!['preload'] = true;
      return fn();
    }

    return of(null);
  }
}
