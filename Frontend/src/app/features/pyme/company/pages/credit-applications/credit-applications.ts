import { Component, effect, inject } from '@angular/core';
import { Store } from './store';
import { ActivatedRoute, Router } from '@angular/router';
import { ConfirmationService } from 'primeng/api';
import { ConfirmDialog } from 'primeng/confirmdialog';

@Component({
  selector: 'app-credit-applications',
  imports: [ConfirmDialog],
  templateUrl: './credit-applications.html',
  styleUrl: './credit-applications.css',
  providers: [Store, ConfirmationService],
})
export default class CreditApplications {
  private readonly _router = inject(Router);
  private readonly _route = inject(ActivatedRoute);
  private readonly _confirmationService = inject(ConfirmationService);

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
