import { Component, input } from '@angular/core';

@Component({
  selector: 'app-fieldset',
  imports: [],
  templateUrl: './fieldset.html',
  styleUrl: './fieldset.css',
})
export class Fieldset {
  readonly legend = input.required<string>();
}
