import { Component, effect, inject } from '@angular/core';
import { KycStore } from './kyc-store';
import { TokenStorage } from '@core/services/token-storage';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-kyc',
  imports: [],
  templateUrl: './kyc.html',
  styleUrl: './kyc.css',
  providers: [KycStore],
})
export default class Kyc {
  private readonly _tokenStorage = inject(TokenStorage);
  private readonly _router = inject(Router);
  private readonly _route = inject(ActivatedRoute);

  readonly store = inject(KycStore);

  constructor() {
    effect(() => {
      const status = this.store.status();

      switch (status) {
        case 'success':
          this._success();
          break;
      }
    });
  }

  async ngOnInit() {
    const user = this._tokenStorage.user();

    if (!user) {
      return this._redirectToAuth();
    }

    await this.store.fetchAllByUserId(user.id);
  }

  private _success() {}

  private _redirectToAuth() {
    this._router.navigate(['..', 'auth'], { relativeTo: this._route });
  }
}
