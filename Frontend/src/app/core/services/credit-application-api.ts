import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from 'environments/environment.development';
import { CreditApplicationResponse } from '../models/credit-application-model';
import { lastValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CreditApplicationApi {
  private readonly _url = `${environment.apiUrl}/api/credit-applications`;
  private readonly _http = inject(HttpClient);

  /**
   * Obtienes todas las aplicaciones de crédito realizadas por el usuario autenticado.
   *
   * @returns listado de aplicaciones de créditos.
   */
  async getAllMyCreditApplications() {
    const call = this._http.get<CreditApplicationResponse[]>(`${this._url}/my`);

    return await lastValueFrom(call);
  }
}
