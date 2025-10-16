import { Component, effect, inject } from '@angular/core';
import { CompanyStore } from './company-store';
import { TokenStorage } from '@core/services/token-storage';
import { Card } from 'primeng/card';
import { Title } from '@components/title/title';
import { ProgressSpinner } from 'primeng/progressspinner';
import { NewKycForm } from './components/new-kyc-form/new-kyc-form';
import { Subtitle } from '@components/subtitle/subtitle';
import { NewCompanyForm } from './components/new-company-form/new-company-form';

@Component({
  selector: 'app-company',
  imports: [Card, Title, ProgressSpinner, NewKycForm, Subtitle, NewCompanyForm],
  templateUrl: './company.html',
  styleUrl: './company.css',
  providers: [CompanyStore],
})
export default class Company {
  private readonly _tokenStorage = inject(TokenStorage);
  readonly store = inject(CompanyStore);

  constructor() {
    effect(async () => {
      const kycStatus = this.store.kycStatus();

      switch (kycStatus) {
        case 'success':
          await this._fetchKycSuccess();
          break;
      }

      const getCompaniesSatus = this.store.getCompaniesStatus();

      switch (getCompaniesSatus) {
        case 'success':
          this._fetchCompaniesSuccess();
          break;
      }
    });
  }

  async ngOnInit() {
    const user = this._tokenStorage.user();

    if (!user) {
      return;
    }

    await this.store.getKycByUserId(user.id);
  }

  private async _fetchKycSuccess() {
    if (this.store.kyc()?.data.length === 0) {
      this.store.setShowNewKycForm(true);
    } else {
      await this.store.getCompanies();
    }
  }

  private _fetchCompaniesSuccess() {
    if (this.store.companies().length === 0) {
      this.store.setShowNewCompanyForm(true);
    }
  }
}
