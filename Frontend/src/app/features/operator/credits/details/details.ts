import { Component, inject } from '@angular/core';
import { Header } from '@features/operator/components/header/header';
import { MenuItem } from 'primeng/api';
import { DetailsStore } from './details-store';
import { ActivatedRoute } from '@angular/router';
import { TableModule } from 'primeng/table';
import { Information } from './components/information/information';
import { RiskScoreCard } from './components/risk-score-card/risk-score-card';
import { LoadingSpinner } from '@components/loading-spinner/loading-spinner';
import { StatusSection } from './components/status-section/status-section';

@Component({
  selector: 'app-details',
  imports: [Header, TableModule, Information, RiskScoreCard, LoadingSpinner, StatusSection],
  templateUrl: './details.html',
  styleUrl: './details.css',
  providers: [DetailsStore],
})
export default class Details {
  private readonly _route = inject(ActivatedRoute);
  readonly store = inject(DetailsStore);

  readonly breadcrumbItems: MenuItem[] = [
    { label: 'Solicitudes de crédito', icon: 'pi pi-home', routerLink: '../' },
    { label: 'Detalles', routerLink: './' },
  ];

  constructor() {
    const id = this._route.snapshot.paramMap.get('id');
    this.store.setId(id!);
  }

  async ngOnInit() {
    await this.store.fetch();
  }
}
