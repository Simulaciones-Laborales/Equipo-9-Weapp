import { Component, effect, inject } from '@angular/core';
import { CompanyStore } from './company-store';
import { TokenStorage } from '@core/services/token-storage';
import { Title } from '@components/title/title';
import { ProgressSpinner } from 'primeng/progressspinner';
import { NewKycForm } from './components/new-kyc-form/new-kyc-form';
import { Subtitle } from '@components/subtitle/subtitle';
import { NewCompanyForm } from './components/new-company-form/new-company-form';
import { KycVerificationFiles } from '@core/types';
import { CompanyRequest } from './models/company-model';
import { CompanyInfo } from './components/company-info/company-info';
import { Card } from 'primeng/card';
import { KYCEntityType } from '@core/models/kyc-model';
import { LayoutStore } from '../layout/layout-store';

@Component({
  selector: 'app-company',
  imports: [Title, ProgressSpinner, NewKycForm, Subtitle, NewCompanyForm, CompanyInfo, Card],
  templateUrl: './company.html',
  styleUrl: './company.css',
  providers: [LayoutStore, CompanyStore],
})
export default class Company {
  private readonly _tokenStorage = inject(TokenStorage);
  private readonly _layoutStore = inject(LayoutStore);
  readonly store = inject(CompanyStore);

  constructor() {
    effect(async () => {
      const kycStatus = this.store.kycStatus();

      switch (kycStatus) {
        case 'success':
          await this._fetchKycSuccess();
          break;
      }
    });

    effect(async () => {
      const kycUserVerification = this.store.kycUserVerificationStatus();

      switch (kycUserVerification) {
        case 'success':
          await this._kycVerificationSuccess();
          break;
        case 'failure':
          this._kycVerificationFailure();
          break;
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

  async ngOnInit() {
    const user = this._tokenStorage.user();

    if (!user) {
      return;
    }

    await this.store.getKycByUserId(user.id);
  }

  private async _fetchKycSuccess() {
    if (this.store.kyc().length === 0) {
      this.store.setShowNewKycForm(true);
    } else {
      await this.store.getCompanies();
    }
  }

  async startUserKycVerification(files: KycVerificationFiles) {
    const user = this._tokenStorage.user();

    if (!user) {
      return;
    }

    await this._startKycVerification(user.id, KYCEntityType.USER, files);
  }

  private async _startKycVerification(
    entityId: string,
    entityType: KYCEntityType,
    files: KycVerificationFiles
  ) {
    this.store.setShowNewKycForm(false);

    await this.store.startUserKycVerification(
      entityId,
      entityType,
      files.selfie!,
      files.dniFront!,
      files.dniBack!
    );
  }

  private async _kycVerificationSuccess() {
    await this.store.getCompanies();
  }

  private _kycVerificationFailure() {
    this.store.setShowNewKycForm(true);
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
