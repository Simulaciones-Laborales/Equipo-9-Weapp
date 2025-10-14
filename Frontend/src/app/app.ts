import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { key } from '@core/utils/http-error-utils';
import { Toast } from 'primeng/toast';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Toast],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  readonly key = key;
}
