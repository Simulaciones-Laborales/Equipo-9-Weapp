import { Component, effect, inject } from '@angular/core';
import { Header } from '@components/header/header';
import { ConfirmationService, MenuItem } from 'primeng/api';
import { Store } from './store';
import { ActivatedRoute, Router } from '@angular/router';
import { Subtitle } from '@components/subtitle/subtitle';
import { NewKycForm } from '@features/components/new-kyc-form/new-kyc-form';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { KYCVerificationStatus } from '@core/models/kyc-model';
import { LoadingSpinner } from '@components/loading-spinner/loading-spinner';

@Component({
  selector: 'app-kyc-verification',
  imports: [Header, Subtitle, NewKycForm, ConfirmDialog, LoadingSpinner],
  templateUrl: './kyc-verification.html',
  styleUrl: './kyc-verification.css',
  providers: [Store, ConfirmationService],
})
export default class KycVerification {
  private readonly _router = inject(Router);
  private readonly _route = inject(ActivatedRoute);
  private readonly _confirmationService = inject(ConfirmationService);

  readonly store = inject(Store);

  readonly menu: MenuItem[] = [
    { icon: 'pi pi-home', routerLink: '../../../' },
    { label: 'Empresas', routerLink: '../../' },
    { label: 'Verificación', routerLink: './' },
  ];

  constructor() {
    const id = this._route.snapshot.paramMap.get('id')!;
    this.store.setCompanyId(id);

    effect(() => {
      const action = this.store.action();

      if (action.redirect) {
        this._router.navigate(['..', 'solicitudes-de-credito'], { relativeTo: this._route });
      }
    });

    effect(() => {
      const newKyc = this.store.newKyc();

      if (newKyc && this.store.newKycStatus() === 'success') {
        const approved = newKyc.status === KYCVerificationStatus.VERIFIED;
        const route = approved ? `../solicitudes-de-credito` : '../../';

        this._confirmationService.confirm({
          message: approved
            ? '¡Su KYC ha sido aprobado!'
            : 'Su KYC está en proceso de verificación.',
          header: approved ? 'Felicitaciones' : 'Atención',
          icon: 'pi pi-info-circle',
          acceptLabel: 'Entendido',
          accept: () => {
            this._router.navigate([route], { relativeTo: this._route });
          },
          rejectVisible: false,
        });
      }
    });
  }

  async ngOnInit() {
    await this.store.fetch();
  }
}
