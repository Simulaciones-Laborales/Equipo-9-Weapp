import { Component, effect, inject } from '@angular/core';
import { CompanyStore } from './company-store';
import { TokenStorage } from '@core/services/token-storage';
import { Card } from 'primeng/card';
import { Title } from '@components/title/title';
import { ProgressSpinner } from 'primeng/progressspinner';
import { NewKycForm } from './components/new-kyc-form/new-kyc-form';
import { Subtitle } from '@components/subtitle/subtitle';

@Component({
  selector: 'app-company',
  imports: [Card, Title, ProgressSpinner, NewKycForm, Subtitle],
  templateUrl: './company.html',
  styleUrl: './company.css',
  providers: [CompanyStore],
})
export default class Company {
  private readonly _tokenStorage = inject(TokenStorage);
  readonly store = inject(CompanyStore);

  constructor() {
    effect(() => {
      const kycStatus = this.store.kycStatus();

      switch (kycStatus) {
        case 'success':
          this._success();
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

  private _success() {
    if (this.store.kyc()?.data.length === 0) {
      this.store.setShowNewKycForm(true);
    }
  }
}
