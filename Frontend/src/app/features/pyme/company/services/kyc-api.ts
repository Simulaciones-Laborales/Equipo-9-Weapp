import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { KYCEntityType, KYCVerificationResponse } from '@core/models/kyc-model';
import { environment } from 'environments/environment.development';
import { lastValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class KycApi {
  private readonly _url = `${environment.apiUrl}/api/kyc`;
  private readonly _http = inject(HttpClient);

  async startVerification(
    entityId: string,
    entityType: KYCEntityType,
    document1: File,
    document2: File,
    document3: File
  ) {
    const formData = new FormData();

    formData.append('document1', document1);
    formData.append('document2', document2);
    formData.append('document3', document3);

    const params = new HttpParams().append('entityId', entityId).append('entityType', entityType);

    const call = this._http.post<KYCVerificationResponse>(`${this._url}/start`, formData, {
      params,
    });

    return await lastValueFrom(call);
  }
}
