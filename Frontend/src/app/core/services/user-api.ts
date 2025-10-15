import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { KYCVerificationResponse } from '@core/models/kyc-model';
import { Response } from '@core/models/response-model';
import { environment } from 'environments/environment.development';
import { lastValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserApi {
  private readonly _url = `${environment.apiUrl}/users`;
  private readonly _http = inject(HttpClient);

  async getAllKYC(userId: string) {
    const call = this._http.get<Response<KYCVerificationResponse[]>>(`${this._url}/${userId}/kyc`);

    return await lastValueFrom(call);
  }
}
