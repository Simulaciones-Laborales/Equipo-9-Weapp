import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { KYCVerificationResponse } from '@core/models/kyc-model';
import { TokenStorage } from '@core/services/token-storage';
import { environment } from 'environments/environment.development';
import { lastValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class KycApi {
  private readonly _url = `${environment.apiUrl}/api/kyc`;
  private readonly _http = inject(HttpClient);
  private readonly _tokenStorage = inject(TokenStorage);

  async startVerification(selfie: File, dniFront: File, dniBack: File) {
    const user = this._tokenStorage.user();

    if (!user) {
      return;
    }

    const formData = new FormData();

    formData.append('selfie', selfie);
    formData.append('dniFront', dniFront);
    formData.append('dniBack', dniBack);

    const params = new HttpParams().set('userId', user.id);

    const call = this._http.post<KYCVerificationResponse>(`${this._url}/start`, {
      params,
      formData,
    });

    return await lastValueFrom(call);
  }
}
