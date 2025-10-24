import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject, input } from '@angular/core';
import { TableModule } from 'primeng/table';
import { Menu } from 'primeng/menu';
import { CompanyResponse } from '../../models/company-model';
import { MenuItem } from 'primeng/api';
import { Button } from 'primeng/button';
import { CompanyStore } from '../../company-store';

@Component({
  selector: 'app-table',
  imports: [TableModule, CurrencyPipe, DatePipe, Menu, Button],
  templateUrl: './table.html',
  styleUrl: './table.css',
})
export class Table {
  private readonly _store = inject(CompanyStore);
  readonly companies = input.required<CompanyResponse[]>();

  readonly items: MenuItem[] = [
    {
      label: 'Gestionar créditos',
      command: (_) => {
        this._store.setShowCompanyCredits(true);
      },
    },
  ];

  setSelectedItem(menu: Menu, event: Event, company: CompanyResponse) {
    menu.toggle(event);
    this._store.setSelectedCompany(company);
  }
}
