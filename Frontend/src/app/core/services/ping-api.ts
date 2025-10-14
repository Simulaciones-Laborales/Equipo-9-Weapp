import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from 'environments/environment.development';

@Injectable({
  providedIn: 'root',
})
export class PingApi {
  private readonly _url = `${environment.apiUrl}/ping`;
  private readonly _http = inject(HttpClient);

  pong() {
    return this._http.get<void>(`${this._url}/pong`);
  }
}
