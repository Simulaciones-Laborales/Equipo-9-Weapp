import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from 'environments/environment.development';
import { CreditApplicationResponse } from '../models/credit-application-model';
import { lastValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CreditApplicationApi {
  private readonly _url = `${environment.apiUrl}/credit-applications`;
  private readonly _http = inject(HttpClient);

  /**
   * Obtienes todas las aplicaciones de crédito realizadas por una compañía.
   *
   * @param companyId ID de la compañía.
   * @returns listado de aplicaciones de créditos.
   */
  async getAllByCompanyId(companyId: string) {
    const call = this._http.get<CreditApplicationResponse[]>(`${this._url}/company/${companyId}`);

    return await lastValueFrom(call);
  }
}
