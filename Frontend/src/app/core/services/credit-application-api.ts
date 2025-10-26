import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from 'environments/environment.development';
import {
  CreditApplicationResponse,
  CreditApplicationStatus,
} from '../models/credit-application-model';
import { lastValueFrom } from 'rxjs';
import { Pageable, PageableResponse } from '@core/types';

@Injectable({
  providedIn: 'root',
})
export class CreditApplicationApi {
  private readonly _url = `${environment.apiUrl}/api/credit-applications`;
  private readonly _http = inject(HttpClient);

  async getAll(status: CreditApplicationStatus | null, pageable: Pageable) {
    let params = new HttpParams().append('pageable', JSON.stringify(pageable));

    if (status) {
      params = params.append('status', status);
    }

    const call = this._http.get<PageableResponse<CreditApplicationResponse>>(this._url, { params });

    return await lastValueFrom(call);
  }

  /**
   * Obtienes todas las aplicaciones de crédito realizadas por el usuario autenticado.
   *
   * @returns listado de aplicaciones de créditos.
   */
  async getAllMyCreditApplications() {
    const call = this._http.get<CreditApplicationResponse[]>(`${this._url}/my`);

    return await lastValueFrom(call);
  }

  /**
   * Permite obtener todas las solicitudes de crédito asociadas a una empresa específica. Solo el propietario de la empresa o un usuario con rol ADMIN/OPERADOR puede acceder a esta información.
   *
   * @param companyId ID (UUID) de la empresa cuyas solicitudes se desean consultar.
   * @returns Devuelve el listado de todas las solicitudes de crédito asociadas a la empresa.
   */
  async getAllByCompanyId(companyId: string) {
    const call = this._http.get<CreditApplicationResponse[]>(`${this._url}/company/${companyId}`);

    return await lastValueFrom(call);
  }
}
