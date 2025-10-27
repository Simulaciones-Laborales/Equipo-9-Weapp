import { Component, input } from '@angular/core';

@Component({
  selector: 'app-dialog-subtitle',
  imports: [],
  templateUrl: './dialog-subtitle.html',
  styleUrl: './dialog-subtitle.css',
})
export class DialogSubtitle {
  readonly headline = input.required<string>();
}
