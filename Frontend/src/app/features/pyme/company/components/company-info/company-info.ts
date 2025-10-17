import { Component, input } from '@angular/core';
import { CompanyResponse } from '../../models/company-model';
import { CurrencyPipe, DatePipe } from '@angular/common';

@Component({
  selector: 'app-company-info',
  imports: [CurrencyPipe, DatePipe],
  templateUrl: './company-info.html',
  styleUrl: './company-info.css',
})
export class CompanyInfo {
  readonly company = input.required<CompanyResponse>();
}
