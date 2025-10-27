import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { TokenStorage } from './token-storage';

@Injectable({
  providedIn: 'root',
})
export class LogoutService {
  private readonly _router = inject(Router);
  private readonly _tokenStorage = inject(TokenStorage);

  logout() {
    this._tokenStorage.clear();
    this._router.navigateByUrl('/');
  }
}
