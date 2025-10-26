import { Component, input } from '@angular/core';
import { Title } from '@components/title/title';

@Component({
  selector: 'app-header',
  imports: [Title],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  readonly headline = input.required<string>();
  readonly description = input.required<string>();
}
