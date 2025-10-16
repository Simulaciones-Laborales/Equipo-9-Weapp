import { Component, input } from '@angular/core';

@Component({
  selector: 'app-subtitle',
  imports: [],
  templateUrl: './subtitle.html',
  styleUrl: './subtitle.css',
})
export class Subtitle {
  readonly headline = input.required<string>();
}
