import { Component, inject } from '@angular/core';
import { Header } from '@features/operator/components/header/header';
import { MenuItem } from 'primeng/api';
import { DetailsStore } from './details-store';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TableModule } from 'primeng/table';
import { Information } from './components/information/information';
import { RiskScoreCard } from './components/risk-score-card/risk-score-card';
import { LoadingSpinner } from '@components/loading-spinner/loading-spinner';
import { StatusSection } from './components/status-section/status-section';
import { Divider } from 'primeng/divider';
import { FilesSection } from './components/files-section/files-section';
import { UpdateCreditApplicationStatusDto } from '@core/models/credit-application-model';

@Component({
  selector: 'app-details',
  imports: [
    Header,
    TableModule,
    Information,
    RiskScoreCard,
    LoadingSpinner,
    StatusSection,
    Divider,
    FilesSection,
    RouterLink,
  ],
  templateUrl: './details.html',
  styleUrl: './details.css',
  providers: [DetailsStore],
})
export default class Details {
  private readonly _route = inject(ActivatedRoute);
  readonly store = inject(DetailsStore);

  readonly breadcrumbItems: MenuItem[] = [
    { icon: 'pi pi-home', routerLink: '/operador' },
    { label: 'Solicitudes de crédito', routerLink: '../' },
    { label: 'Detalles', routerLink: './' },
  ];

  constructor() {
    const id = this._route.snapshot.paramMap.get('id');
    this.store.setId(id!);
  }

  async ngOnInit() {
    await this.store.fetch();
  }

  async onUpdateStatus(dto: UpdateCreditApplicationStatusDto) {
    await this.store.updateStatus(dto);
  }
}
