import { Component, inject } from '@angular/core';
import { Title } from '@components/title/title';
import { Card } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { ProgressSpinner } from 'primeng/progressspinner';
import { CreditStore } from './credit-store';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Button } from 'primeng/button';

@Component({
  selector: 'app-credits',
  imports: [Title, Card, TableModule, CurrencyPipe, DatePipe, ProgressSpinner, Button],
  templateUrl: './credits.html',
  styleUrl: './credits.css',
  providers: [CreditStore],
})
export default class Credits {
  readonly store = inject(CreditStore);

  async ngOnInit() {
    await this.store.fetchAllByCompanyId('dsf');
  }
}
