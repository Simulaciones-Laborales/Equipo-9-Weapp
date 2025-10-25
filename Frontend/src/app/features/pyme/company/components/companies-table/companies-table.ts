import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject, input } from '@angular/core';
import { TableModule } from 'primeng/table';
import { Menu } from 'primeng/menu';
import { CompanyResponse } from '../../models/company-model';
import { MenuItem } from 'primeng/api';
import { Button } from 'primeng/button';
import { CompanyStore } from '../../company-store';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-companies-table',
  imports: [TableModule, CurrencyPipe, DatePipe, Menu, Button],
  templateUrl: './companies-table.html',
  styleUrl: './companies-table.css',
})
export class CompaniesTable {
  private readonly _store = inject(CompanyStore);
  private readonly _router = inject(Router);
  private readonly _route = inject(ActivatedRoute);
  readonly companies = input.required<CompanyResponse[]>();

  readonly items: MenuItem[] = [
    {
      label: 'Gestionar créditos',
      command: (_) => {
        const { idCompany } = this._store.selectedCompany()!;
        this._router.navigate([`${idCompany}`, 'solicitudes-de-credito'], {
          relativeTo: this._route,
        });
      },
    },
  ];

  setSelectedItem(menu: Menu, event: Event, company: CompanyResponse) {
    menu.toggle(event);
    this._store.setSelectedCompany(company);
  }
}
