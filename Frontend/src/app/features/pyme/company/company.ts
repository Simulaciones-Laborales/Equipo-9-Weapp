import { Component, effect, inject } from '@angular/core';
import { CompanyStore } from './company-store';
import { Title } from '@components/title/title';
import { ProgressSpinner } from 'primeng/progressspinner';
import { Subtitle } from '@components/subtitle/subtitle';
import { NewCompanyForm } from './components/new-company-form/new-company-form';
import { CompanyRequest } from './models/company-model';
import { CompanyInfo } from './components/company-info/company-info';
import { Card } from 'primeng/card';
import { LayoutStore } from '../layout/layout-store';
import { Button } from 'primeng/button';

@Component({
  selector: 'app-company',
  imports: [Title, ProgressSpinner, Subtitle, NewCompanyForm, CompanyInfo, Card, Button],
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

  async createNewCompany(req: CompanyRequest) {
    await this.store.createCompany(req);
  }
}
