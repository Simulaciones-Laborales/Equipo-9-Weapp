import { Component, inject } from '@angular/core';
import { ListStore } from './list-store';
import { Header } from '@components/header/header';
import { MenuItem } from 'primeng/api';
import { RouterLink } from '@angular/router';
import { ButtonDirective } from 'primeng/button';
import { CompaniesTable } from './companies-table/companies-table';
import { LoadingSpinner } from '@components/loading-spinner/loading-spinner';
import { TableModule } from 'primeng/table';
import { Subtitle } from '@components/subtitle/subtitle';

@Component({
  selector: 'app-list',
  imports: [
    Header,
    RouterLink,
    ButtonDirective,
    CompaniesTable,
    LoadingSpinner,
    TableModule,
    Subtitle,
  ],
  templateUrl: './list.html',
  styleUrl: './list.css',
  providers: [ListStore],
})
export default class List {
  readonly store = inject(ListStore);

  readonly menu: MenuItem[] = [
    { icon: 'pi pi-home', routerLink: '../' },
    { label: 'Empresas', routerLink: './' },
  ];

  async ngOnInit() {
    await this.store.fetch();
  }
}
