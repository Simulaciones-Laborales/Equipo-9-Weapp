import { Component, effect, inject } from '@angular/core';
import { Store } from './store';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ConfirmationService, MenuItem } from 'primeng/api';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { Header } from '@components/header/header';
import { LoadingSpinner } from '@components/loading-spinner/loading-spinner';
import { CreditsTable } from './credits-table/credits-table';
import { Subtitle } from '@components/subtitle/subtitle';
import { Button } from 'primeng/button';

@Component({
  selector: 'app-credit-applications',
  imports: [ConfirmDialog, Header, LoadingSpinner, CreditsTable, Subtitle, Button, RouterLink],
  templateUrl: './credit-applications.html',
  styleUrl: './credit-applications.css',
  providers: [Store, ConfirmationService],
})
export default class CreditApplications {
  private readonly _router = inject(Router);
  private readonly _route = inject(ActivatedRoute);
  private readonly _confirmationService = inject(ConfirmationService);

  readonly menu: MenuItem[] = [
    { icon: 'pi pi-home', routerLink: '../../../' },
    { label: 'Empresas', routerLink: '../../' },
    { label: 'Solicitudes de crédito', routerLink: './' },
  ];

  readonly store = inject(Store);

  constructor() {
    const id = this._route.snapshot.paramMap.get('id')!;
    this.store.setCompanyId(id);

    effect(() => {
      if (this.store.redirect()) {
        this._confirmationService.confirm({
          message:
            'Tu empresa aún no está verificada.<p>A continuación, serás redireccionado para continuar con el proceso de verificación.</p>',
          header: 'Atención',
          icon: 'pi pi-exclamation-circle',
          accept: () => {
            this._router.navigate(['..', 'verificacion'], { relativeTo: this._route });
          },
          acceptLabel: 'Entendido',
          rejectVisible: false,
        });
      }
    });
  }

  async ngOnInit() {
    await this.store.fetch();
  }
}
