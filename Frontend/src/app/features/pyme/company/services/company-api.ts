import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from 'environments/environment.development';
import { CompanyRequest, CompanyResponse } from '../models/company-model';
import { lastValueFrom } from 'rxjs';
import { CreditApplicationResponse } from '@core/models/credit-application-model';
import { KYCVerificationResponse } from '@core/models/kyc-model';

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

  /**
   * Consulta la información de KYC (Know Your Customer) de una empresa específica. Solo si la empresa pertenece al usuario autenticado.
   *
   * @param companyId ID de la empresa.
   * @returns kyc de la empresa.
   */
  async getKyc(companyId: string) {
    const call = this._http.get<KYCVerificationResponse>(`${this._url}/${companyId}/kyc`);

    return await lastValueFrom(call);
  }

  /**
   * Devuelve una lista de las solicitudes de crédito de una empresa específica. Solo si la empresa pertenece al usuario autenticado.
   *
   * @param companyId ID de la empresa.
   * @returns listado de solicitudes de crédito.
   */
  async getAllCreditApplications(companyId: string) {
    const call = this._http.get<CreditApplicationResponse[]>(
      `${this._url}/${companyId}/credit-applications`
    );

    return await lastValueFrom(call);
  }
}
