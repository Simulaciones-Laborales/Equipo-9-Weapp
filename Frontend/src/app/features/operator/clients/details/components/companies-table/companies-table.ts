import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, input, output } from '@angular/core';
import { CompanyResponse } from '@features/pyme/company/models/company-model';
import { TableModule } from 'primeng/table';
import { Button } from 'primeng/button';

@Component({
  selector: 'app-companies-table',
  imports: [TableModule, CurrencyPipe, DatePipe, Button],
  templateUrl: './companies-table.html',
  styleUrl: './companies-table.css',
})
export class CompaniesTable {
  readonly onVerificationClick = output<string>();
  readonly companies = input.required<CompanyResponse[]>();
}
