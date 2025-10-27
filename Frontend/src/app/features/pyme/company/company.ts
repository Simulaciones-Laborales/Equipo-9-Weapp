import { Component, effect, inject } from '@angular/core';
import { CompanyStore } from './company-store';
import { Title } from '@components/title/title';
import { LayoutStore } from '../layout/layout-store';
import { TableModule } from 'primeng/table';
import { KycWarningMessage } from './components/kyc-warning-message/kyc-warning-message';
import { NewCompanySection } from './components/new-company-section/new-company-section';
import { CompaniesListSection } from './components/companies-list-section/companies-list-section';
import { LoadingSpinner } from '@components/loading-spinner/loading-spinner';

@Component({
  selector: 'app-company',
  imports: [
    Title,
    TableModule,
    KycWarningMessage,
    NewCompanySection,
    CompaniesListSection,
    LoadingSpinner,
  ],
  templateUrl: './company.html',
  styleUrl: './company.css',
  providers: [CompanyStore],
})
export default class Company {
  readonly layoutStore = inject(LayoutStore);
  readonly store = inject(CompanyStore);

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
