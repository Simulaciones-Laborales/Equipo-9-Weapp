import { Component, input } from '@angular/core';

@Component({
  selector: 'app-info-item',
  imports: [],
  templateUrl: './info-item.html',
  styleUrl: './info-item.css',
})
export class InfoItem {
  readonly name = input.required<string>();
  readonly value = input.required<string | Date | undefined>();
}
