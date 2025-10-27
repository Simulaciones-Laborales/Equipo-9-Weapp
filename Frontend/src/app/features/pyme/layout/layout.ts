import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './components/navbar/navbar';
import { LayoutStore } from './layout-store';
import { LoadingSpinner } from '@components/loading-spinner/loading-spinner';

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, Navbar, LoadingSpinner],
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
