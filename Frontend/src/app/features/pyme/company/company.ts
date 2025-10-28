import { Component, effect, inject } from '@angular/core';
import { CompanyStore } from './company-store';
import { LayoutStore } from '../layout/layout-store';
import { TableModule } from 'primeng/table';
import { KycWarningMessage } from './components/kyc-warning-message/kyc-warning-message';
import { NewCompanySection } from './components/new-company-section/new-company-section';
import { CompaniesListSection } from './components/companies-list-section/companies-list-section';
import { LoadingSpinner } from '@components/loading-spinner/loading-spinner';
import { Header } from '@components/header/header';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-company',
  imports: [
    TableModule,
    KycWarningMessage,
    NewCompanySection,
    CompaniesListSection,
    LoadingSpinner,
    Header,
  ],
  templateUrl: './company.html',
  styleUrl: './company.css',
  providers: [CompanyStore],
})
export default class Company {
  readonly layoutStore = inject(LayoutStore);
  readonly store = inject(CompanyStore);

  readonly menu: MenuItem[] = [
    { icon: 'pi pi-home', routerLink: '../' },
    { label: 'Empresas', routerLink: './' },
  ];

  constructor() {
    effect(async () => {
      const status = this.layoutStore.userKycStatus();

      if (status.name === 'KYC Verificado') {
        this.store.setShowKycMessage(false);
        await this.store.getCompanies();
      } else {
        this.store.setShowKycMessage(true);
      }
    });

    effect(async () => {
      const getCompaniesSatus = this.store.getCompaniesStatus();

      switch (getCompaniesSatus) {
        case 'success':
          this._fetchCompaniesSuccess();
          break;
      }
    });
  }

  private _fetchCompaniesSuccess() {
    if (this.store.companies().length === 0) {
      this.store.setShowNewCompanyForm(true);
    } else {
      this.store.setShowCompanies(true);
    }
  }
}
