import { Component, inject } from '@angular/core';
import { Header } from '../components/header/header';
import { Menu } from 'primeng/menu';
import { MenuItem } from 'primeng/api';
import { Subtitle } from '@components/subtitle/subtitle';
import { CreditsTable } from './components/credits-table/credits-table';
import { CreditsStore } from './credits-store';
import { CreditApplicationStatus } from '@core/models/credit-application-model';
import { Pageable } from '@core/types';
import { LoadingSpinner } from '@components/loading-spinner/loading-spinner';
import { PaginatorState } from 'primeng/paginator';

@Component({
  selector: 'app-credits',
  imports: [Header, Menu, Subtitle, CreditsTable, LoadingSpinner],
  templateUrl: './credits.html',
  styleUrl: './credits.css',
  providers: [CreditsStore],
})
export default class Credits {
  readonly breadcrumbItems: MenuItem[] = [
    { icon: 'pi pi-home', routerLink: '/operador' },
    { label: 'Solicitudes de crédito', routerLink: './' },
  ];

  readonly store = inject(CreditsStore);

  readonly filterItems: MenuItem[] = [
    {
      label: 'Todas',
      command: (_) => {
        this._fetchByFilter(null);
      },
    },
    {
      label: 'Pendientes',
      command: (_) => {
        this._fetchByFilter(CreditApplicationStatus.PENDING);
      },
    },
    {
      label: 'En Revisión',
      command: (_) => {
        this._fetchByFilter(CreditApplicationStatus.UNDER_REVIEW);
      },
    },
    {
      label: 'Aprobadas',
      command: (_) => {
        this._fetchByFilter(CreditApplicationStatus.APPROVED);
      },
    },
    {
      label: 'Rechazadas',
      command: (_) => {
        this._fetchByFilter(CreditApplicationStatus.REJECTED);
      },
    },
    {
      label: 'Canceladas',
      command: (_) => {
        this._fetchByFilter(CreditApplicationStatus.CANCELLED);
      },
    },
  ];

  async ngOnInit() {
    await this.store.fetchAll(null, {
      page: 1,
      size: 5,
      sort: ['createdAt'],
    });
  }

  async fetchByPageable(paginator: PaginatorState) {
    const { status, credits } = this.store;

    const pageable: Pageable = {
      page: paginator.page!,
      size: credits()!.size,
      sort: ['createdAt'],
    };

    await this.store.fetchAll(status(), pageable);
  }

  private async _fetchByFilter(status: CreditApplicationStatus | null) {
    const currentPageable = this.store.credits()!;

    const pageable: Pageable = {
      page: currentPageable.pageable.pageNumber,
      size: currentPageable.size,
      sort: ['createdAt'],
    };

    await this.store.fetchAll(status, pageable);
  }
}
