import { Injectable } from '@angular/core';
import { JwtPayload } from '@core/models/token-model';
import { LoginRes } from '@features/pyme/auth/models/auth-model';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root',
})
export class TokenStorage {
  private static readonly KEY = '1c3cr54m';

  save(res: LoginRes) {
    sessionStorage.setItem(TokenStorage.KEY, JSON.stringify(res));
  }

  clear() {
    sessionStorage.removeItem(TokenStorage.KEY);
  }

  token() {
    return this._load((json: LoginRes) => json.token);
  }

  decoded() {
    return this._load((json: LoginRes) => jwtDecode<JwtPayload>(json.token));
  }

  user() {
    return this._load((json: LoginRes) => json);
  }

  private _load<T>(callback: (json: LoginRes) => T) {
    const data = this._get();

    if (data === null) {
      return null;
    }

    try {
      const json = JSON.parse(data) as LoginRes;
      return callback(json);
    } catch (e) {
      throw Error(`Error leyendo el token: ${e instanceof Error ? e.message : ''}`);
    }
  }

  private _get() {
    return sessionStorage.getItem(TokenStorage.KEY);
  }
}
