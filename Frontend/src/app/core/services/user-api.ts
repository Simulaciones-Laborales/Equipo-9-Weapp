import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { KYCVerificationResponse, KYCVerificationStatus } from '@core/models/kyc-model';
import { Response } from '@core/models/response-model';
import { User } from '@core/models/user-model';
import { CompanyResponse } from '@features/pyme/company/models/company-model';
import { environment } from 'environments/environment.development';
import { lastValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserApi {
  private readonly _url = `${environment.apiUrl}/user`;
  private readonly _http = inject(HttpClient);

  async getAllKycByStatus(userId: string, status: KYCVerificationStatus | null) {
    let params = new HttpParams();

    if (status) {
      params = new HttpParams().append('status', status);
    }

    const call = this._http.get<Response<KYCVerificationResponse[]>>(`${this._url}/${userId}/kyc`, {
      params,
    });

    return await lastValueFrom(call);
  }

  /* async getAllKYC(userId: string) {
    const call = this._http.get<Response<KYCVerificationResponse[]>>(
      `${this._url}/${userId}/kyc/all`
    );

    return await lastValueFrom(call);
  } */

  /**
   * Permite recuperar una lista de todos los usuarios.
   *
   * @returns Devuelve el listado de usuarios.
   */
  async getAll() {
    const call = this._http.get<Response<User[]>>(`${this._url}/list`);

    return await lastValueFrom(call);
  }

  async getById(userId: string) {
    const call = this._http.get<Response<User>>(`${this._url}/${userId}`);

    return await lastValueFrom(call);
  }

  /**
   * Devuelve los datos del usuario que ha iniciado sesión actualmente.
   *
   * @returns usuario autenticado.
   */
  async getMe() {
    const call = this._http.get<Response<User>>(this._url);

    return await lastValueFrom(call);
  }

  async getCompanies(userId: string) {
    const call = this._http.get<Response<CompanyResponse[]>>(`${this._url}/${userId}/companies`);

    return await lastValueFrom(call);
  }
}
