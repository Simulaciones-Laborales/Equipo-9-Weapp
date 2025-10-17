import { Component, inject } from '@angular/core';
import { Title } from '@components/title/title';
import { TableModule } from 'primeng/table';
import { ProgressSpinner } from 'primeng/progressspinner';
import { CreditStore } from './credit-store';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Button } from 'primeng/button';

@Component({
  selector: 'app-credits',
  imports: [Title, TableModule, CurrencyPipe, DatePipe, ProgressSpinner, Button],
  templateUrl: './credits.html',
  styleUrl: './credits.css',
  providers: [CreditStore],
})
export default class Credits {
  readonly store = inject(CreditStore);

  async ngOnInit() {
    await this.store.fetchAllMyCredits();
  }
}
