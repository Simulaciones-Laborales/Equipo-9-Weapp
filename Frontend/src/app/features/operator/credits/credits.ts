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

@Component({
  selector: 'app-credits',
  imports: [Header, Menu, Subtitle, CreditsTable, LoadingSpinner],
  templateUrl: './credits.html',
  styleUrl: './credits.css',
  providers: [CreditsStore],
})
export default class Credits {
  readonly store = inject(CreditsStore);

  readonly filterItems: MenuItem[] = [
    {
      label: 'Todas',
      command: (_) => {
        this.fetchByFilter(null);
      },
    },
    {
      label: 'Pendientes',
      command: (_) => {
        this.fetchByFilter(CreditApplicationStatus.PENDING);
      },
    },
    {
      label: 'En Revisión',
      command: (_) => {
        this.fetchByFilter(CreditApplicationStatus.UNDER_REVIEW);
      },
    },
    {
      label: 'Aprobadas',
      command: (_) => {
        this.fetchByFilter(CreditApplicationStatus.APPROVED);
      },
    },
    {
      label: 'Rechazadas',
      command: (_) => {
        this.fetchByFilter(CreditApplicationStatus.REJECTED);
      },
    },
    {
      label: 'Canceladas',
      command: (_) => {
        this.fetchByFilter(CreditApplicationStatus.CANCELLED);
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

  async fetchByFilter(status: CreditApplicationStatus | null) {
    const currentPageable = this.store.credits()!;

    const pageable: Pageable = {
      page: currentPageable.pageable.pageNumber,
      size: currentPageable.size,
      sort: ['createdAt'],
    };

    await this.store.fetchAll(status, pageable);
  }
}
