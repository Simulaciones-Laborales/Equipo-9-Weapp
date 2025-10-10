import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from 'environments/environment.development';
import { LoginReq, LoginRes, RegisterModel } from '../models/auth-model';
import { Response } from '@core/models/response-model';

@Injectable({
  providedIn: 'root',
})
export class AuthApi {
  private readonly _url = `${environment.apiUrl}/auth`;
  private readonly _http = inject(HttpClient);

  /**
   * Genera un token para el reseteo de contraseña y lo envía al email del usuario.
   *
   * @param email correo electrónico del usuario a generar el token.
   * @returns un json con el token generado.
   */
  generateResetToken(email: string) {
    return this._http.post<{ token: string }>(`${this._url}/generate-reset-token`, { email });
  }

  /**
   * Autentica a un usuario con sus credenciales y devuelve un token de autenticación.
   *
   * @param dto credenciales del usuario para iniciar sesión.
   * @returns json con data del usuario y el token de acceso.
   */
  login(dto: LoginReq) {
    return this._http.post<Response<LoginRes>>(`${this._url}/login`, dto);
  }

  /**
   * Permite registrar un nuevo usuario PYME al sistema.
   * Se deben proporcionar los datos completos del usuario, incluyendo: nombres, apellidos, correo electrónico, contacto.
   *
   * @param dto
   * @returns
   */
  register(dto: RegisterModel) {
    return this._http.post<Response<LoginRes>>(`${this._url}/register`, dto);
  }
}
