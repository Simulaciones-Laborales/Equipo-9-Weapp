import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from 'environments/environment.development';
import { lastValueFrom } from 'rxjs';
import { DashboardDto } from './model';
import { Response } from '@core/models/response-model';

@Injectable({
  providedIn: 'root',
})
export class Api {
  private readonly _url = `${environment.apiUrl}/dashboard`;
  private readonly _http = inject(HttpClient);

  async fetch() {
    const call = this._http.get<Response<DashboardDto>>(this._url);

    return await lastValueFrom(call);
  }
}
