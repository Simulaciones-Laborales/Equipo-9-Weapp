import { Component, effect, inject } from '@angular/core';
import { Title } from '@components/title/title';
import { TableModule } from 'primeng/table';
import { ProgressSpinner } from 'primeng/progressspinner';
import { CreditStore } from './credit-store';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Button } from 'primeng/button';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-credits',
  imports: [Title, TableModule, CurrencyPipe, DatePipe, ProgressSpinner, Button],
  templateUrl: './credits.html',
  styleUrl: './credits.css',
  providers: [CreditStore],
})
export default class Credits {
  private readonly _route = inject(ActivatedRoute);
  readonly store = inject(CreditStore);

  constructor() {
    effect(() => {
      switch (this.store.errorStatus()) {
        case 403:
          console.log('sjdkfl');
          break;
      }
    });
  }

  async ngOnInit() {
    const companyId = this._route.snapshot.queryParamMap.get('id');
    await this.store.fetchAll(companyId!);
  }
}
