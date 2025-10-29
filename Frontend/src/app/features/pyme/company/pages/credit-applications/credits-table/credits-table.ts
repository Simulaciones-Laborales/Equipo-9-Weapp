import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CreditApplicationResponse } from '@core/models/credit-application-model';
import { CreditApplicationPurposePipe } from '@pipes/credit-application-purpose-pipe';
import { CreditApplicationStatusPipe } from '@pipes/credit-application-status-pipe';
import { TableModule } from 'primeng/table';

@Component({
  selector: 'app-credits-table',
  imports: [
    TableModule,
    CurrencyPipe,
    CreditApplicationPurposePipe,
    CreditApplicationStatusPipe,
    DatePipe,
    RouterLink,
  ],
  templateUrl: './credits-table.html',
  styleUrl: './credits-table.css',
})
export class CreditsTable {
  readonly credits = input.required<CreditApplicationResponse[]>();
}
