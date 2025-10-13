import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from 'environments/environment.development';
import { LoginReq, LoginRes, RegisterModel } from '../models/auth-model';
import { Response } from '@core/models/response-model';
import { lastValueFrom } from 'rxjs';

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
  async generateResetToken(email: string) {
    const call = this._http.post<{ token: string }>(`${this._url}/generate-reset-token`, { email });
    return await lastValueFrom(call);
  }

  /**
   * Autentica a un usuario con sus credenciales y devuelve un token de autenticación.
   *
   * @param dto credenciales del usuario para iniciar sesión.
   * @returns json con data del usuario y el token de acceso.
   */
  async login(dto: LoginReq) {
    const call = this._http.post<Response<LoginRes>>(`${this._url}/login`, dto);
    return lastValueFrom(call);
  }

  /**
   * Permite registrar un nuevo usuario PYME al sistema.
   * Se deben proporcionar los datos completos del usuario, incluyendo: nombres, apellidos, correo electrónico, contacto.
   *
   * @param dto datos de registro.
   * @returns datos del usuario.
   */
  async register(dto: RegisterModel) {
    const call = this._http.post<Response<LoginRes>>(`${this._url}/register`, dto);
    return await lastValueFrom(call);
  }
}
