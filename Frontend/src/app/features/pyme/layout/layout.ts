import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './components/navbar/navbar';
import { LayoutStore } from './layout-store';
import { ProgressSpinner } from 'primeng/progressspinner';

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, Navbar, ProgressSpinner],
  templateUrl: './layout.html',
  styleUrl: './layout.css',
  providers: [LayoutStore],
})
export default class Layout {
  readonly store = inject(LayoutStore);

  async ngOnInit() {
    await this.store.fetchInitialData();
  }
}
