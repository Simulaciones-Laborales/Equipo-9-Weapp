import { Component, input } from '@angular/core';
import { Title } from '@components/title/title';
import { MenuItem } from 'primeng/api';
import { Breadcrumb } from 'primeng/breadcrumb';

@Component({
  selector: 'app-header',
  imports: [Title, Breadcrumb],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  readonly menuItems = input.required<MenuItem[]>();
  readonly headline = input.required<string>();
  readonly description = input.required<string>();
}
