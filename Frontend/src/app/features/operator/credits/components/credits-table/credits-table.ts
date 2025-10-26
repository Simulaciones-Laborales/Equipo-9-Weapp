import { Component, input, signal } from '@angular/core';
import { Paginator } from 'primeng/paginator';
import { TableModule } from 'primeng/table';
import { Button } from 'primeng/button';
import { Menu } from 'primeng/menu';
import { CreditApplicationResponse } from '@core/models/credit-application-model';
import { PageableResponse } from '@core/types';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { CreditApplicationPurposePipe } from 'app/pipes/credit-application-purpose-pipe';
import { CreditApplicationStatusPipe } from 'app/pipes/credit-application-status-pipe';

@Component({
  selector: 'app-credits-table',
  imports: [
    TableModule,
    Paginator,
    Button,
    CurrencyPipe,
    DatePipe,
    CreditApplicationPurposePipe,
    CreditApplicationStatusPipe,
  ],
  templateUrl: './credits-table.html',
  styleUrl: './credits-table.css',
})
export class CreditsTable {
  readonly credits = input.required<PageableResponse<CreditApplicationResponse> | null>();
  readonly menu = input.required<Menu>();
}
