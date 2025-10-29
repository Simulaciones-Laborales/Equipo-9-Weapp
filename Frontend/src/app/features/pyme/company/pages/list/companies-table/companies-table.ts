import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, input } from '@angular/core';
import { TableModule } from 'primeng/table';
import { CompanyResponse } from '../../../models/company-model';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-companies-table',
  imports: [TableModule, CurrencyPipe, DatePipe, RouterLink],
  templateUrl: './companies-table.html',
  styleUrl: './companies-table.css',
})
export class CompaniesTable {
  readonly companies = input.required<CompanyResponse[]>();
}
