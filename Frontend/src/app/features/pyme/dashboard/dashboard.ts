import { Component, effect, inject } from '@angular/core';
import { Title } from '@components/title/title';
import { TokenStorage } from '@core/services/token-storage';
import { Subtitle } from '@components/subtitle/subtitle';
import { Button } from 'primeng/button';
import { Divider } from 'primeng/divider';
import { Router } from '@angular/router';
import { DashboardStore } from './dashboard-store';
import { NewKycForm } from '@features/components/new-kyc-form/new-kyc-form';
import { LayoutStore } from '../layout/layout-store';

@Component({
  selector: 'app-dashboard',
  imports: [Title, Subtitle, Button, Divider, NewKycForm],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
  providers: [DashboardStore],
})
export default class Dashboard {
  private readonly _tokenStorage = inject(TokenStorage);
  private readonly _router = inject(Router);

  readonly layoutStorage = inject(LayoutStore);
  readonly store = inject(DashboardStore);

  constructor() {
    effect(async () => {
      const status = this.layoutStorage.userKycStatus();

      const showKycButton = status.name === 'Sin KYC' || status.name === 'KYC Rechazado';

      this.store.setShowKycButton(showKycButton);
    });

    effect(() => {
      if (this.store.startVerificationStatus() === 'success') {
        this.store.setShowUserKycForm(false);
      }
    });
  }

  get user() {
    return this._tokenStorage.user();
  }

  logout() {
    this._tokenStorage.clear();
    this._router.navigateByUrl('pyme/auth');
  }
}
