import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from 'environments/environment.development';
import {
  CreateCreditApplicationDto,
  CreditApplicationHistory,
  CreditApplicationResponse,
  CreditApplicationStatus,
  UpdateCreditApplicationStatusDto,
} from '../models/credit-application-model';
import { lastValueFrom } from 'rxjs';
import { Pageable, PageableResponse } from '@core/types';

@Injectable({
  providedIn: 'root',
})
export class CreditApplicationApi {
  private readonly _url = `${environment.apiUrl}/api/credit-applications`;
  private readonly _http = inject(HttpClient);

  async create(dto: CreateCreditApplicationDto, files: File[]) {
    const formData = new FormData();

    formData.append('data', JSON.stringify(dto));
    files.forEach((file) => formData.append('files', file, file.name));

    const call = this._http.post<CreditApplicationResponse>(this._url, formData);

    return await lastValueFrom(call);
  }

  async getAll(status: CreditApplicationStatus | null, pageable: Pageable) {
    let params = new HttpParams().append('pageable', JSON.stringify(pageable));

    if (status) {
      params = params.append('status', status);
    }

    const call = this._http.get<PageableResponse<CreditApplicationResponse>>(`${this._url}/all`, {
      params,
    });

    return await lastValueFrom(call);
  }

  async getById(id: string) {
    const call = this._http.get<CreditApplicationResponse>(`${this._url}/${id}`);

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

  /**
   * Obtiene el registro de auditoría y eventos (cambios de estado, comentarios, actualizaciones) para una solicitud específica. La respuesta está paginada.
   *
   * @param id ID de la solicitud de crédito.
   * @param pageable solicitud de paginación.
   * @returns paginado con el historial de solicitudes.
   */
  async getHistory(id: string, pageable: Pageable) {
    const params = new HttpParams().append('pageable', JSON.stringify(pageable));

    const call = this._http.get<PageableResponse<CreditApplicationHistory>>(
      `${this._url}/${id}/history`,
      { params }
    );

    return await lastValueFrom(call);
  }

  /**
   * Permite cambiar el estado de una solicitud de crédito existente. Solo el propietario de la empresa asociada a la solicitud o un usuario con rol ADMIN/OPERADOR puede realizar esta acción. Se pueden agregar comentarios opcionales del operador.
   *
   * @param id ID de la solicitud de crédito.
   */
  async updateStatus(id: string, dto: UpdateCreditApplicationStatusDto) {
    const call = this._http.put<CreditApplicationResponse>(`${this._url}/${id}/status`, dto);

    return await lastValueFrom(call);
  }
}
