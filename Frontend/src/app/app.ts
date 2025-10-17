import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { PingApi } from '@core/services/ping-api';
import { key } from '@core/utils/http-utils';
import { Toast } from 'primeng/toast';
import { interval } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Toast],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  private readonly _pingApi = inject(PingApi);
  readonly key = key;

  ngOnInit() {
    interval(300000).subscribe(() => this._pingApi.pong().subscribe());
  }
}
