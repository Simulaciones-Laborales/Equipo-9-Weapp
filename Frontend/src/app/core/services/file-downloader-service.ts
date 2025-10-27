import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class FileDownloaderService {
  private readonly _http = inject(HttpClient);

  download(url: string) {
    return this._http.get(url, { responseType: 'blob' });
  }
}
