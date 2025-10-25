import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, input } from '@angular/core';
import { CreditApplicationResponse } from '@core/models/credit-application-model';
import { TableModule } from 'primeng/table';

@Component({
  selector: 'app-credits-table',
  imports: [TableModule, DatePipe, CurrencyPipe],
  templateUrl: './credits-table.html',
  styleUrl: './credits-table.css',
})
export class CreditsTable {
  readonly credits = input.required<CreditApplicationResponse[]>();
}
