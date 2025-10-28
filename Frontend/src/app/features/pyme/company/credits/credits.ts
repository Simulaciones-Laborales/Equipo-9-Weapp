import { Component, effect, inject } from '@angular/core';
import { CreditStore } from './credit-store';
import { ActivatedRoute } from '@angular/router';
import { LoadingSpinner } from '@components/loading-spinner/loading-spinner';
import { CreditsSection } from './components/credits-section/credits-section';
import { KycSection } from './components/kyc-section/kyc-section';
import { Breadcrumb } from 'primeng/breadcrumb';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-credits',
  imports: [LoadingSpinner, CreditsSection, KycSection, Breadcrumb],
  templateUrl: './credits.html',
  styleUrl: './credits.css',
  providers: [CreditStore],
})
export default class Credits {
  private readonly _route = inject(ActivatedRoute);
  readonly store = inject(CreditStore);

  readonly breadcrumItems: MenuItem[] = [
    {
      label: 'Empresas',
      routerLink: '../../',
    },
    {
      label: 'Solicitudes de crédito',
      routerLink: './',
    },
  ];

  constructor() {
    const companyId = this._route.snapshot.paramMap.get('id');
    this.store.setCompanyId(companyId!);

    effect(() => {
      switch (this.store.errorStatusCode()) {
        case 403:
          this.store.setShow('kyc');
          break;
      }
    });

    effect(async () => {
      switch (this.store.kycVerificationStatus()) {
        case 'success':
          await this.store.fetchAll();
          break;
      }
    });
  }

  async ngOnInit() {
    await this.store.fetchAll();
  }
}
