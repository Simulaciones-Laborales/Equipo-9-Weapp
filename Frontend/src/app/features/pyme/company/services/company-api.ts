import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from 'environments/environment.development';
import { CompanyRequest, CompanyResponse } from '../models/company-model';
import { lastValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CompanyApi {
  private readonly _url = `${environment.apiUrl}/api/companies`;
  private readonly _http = inject(HttpClient);

  /**
   * Registra una nueva empresa al usuario autenticado.
   *
   * @param req data para crear la empresa.
   * @returns la empresa registrada desde la base de datos.
   */
  async create(req: CompanyRequest) {
    const call = this._http.post<CompanyResponse>(this._url, req);

    return await lastValueFrom(call);
  }

  /**
   * Obtiene todas las empresas del usuario autenticado.
   *
   * @returns un listado de empresas.
   */
  async getAllByAuthenticatedUser() {
    const call = this._http.get<CompanyResponse[]>(this._url);

    return await lastValueFrom(call);
  }
}
