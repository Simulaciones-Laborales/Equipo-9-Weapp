import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { KYCVerificationResponse, KYCVerificationStatus } from '@core/models/kyc-model';
import { Response } from '@core/models/response-model';
import { environment } from 'environments/environment.development';
import { lastValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserApi {
  private readonly _url = `${environment.apiUrl}/user`;
  private readonly _http = inject(HttpClient);

  async getAllKycByStatus(userId: string, status: KYCVerificationStatus) {
    const params = new HttpParams().append('status', status);

    const call = this._http.get<Response<KYCVerificationResponse[]>>(`${this._url}/${userId}/kyc`, {
      params,
    });

    return await lastValueFrom(call);
  }

  async getAllKYC(userId: string) {
    const call = this._http.get<Response<KYCVerificationResponse[]>>(
      `${this._url}/${userId}/kyc/all`
    );

    return await lastValueFrom(call);
  }
}
