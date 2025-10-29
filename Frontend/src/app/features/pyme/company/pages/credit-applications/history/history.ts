import { Component, inject } from '@angular/core';
import { Header } from '@components/header/header';
import { Subtitle } from '@components/subtitle/subtitle';
import { MenuItem } from 'primeng/api';
import { CreditApplicationHistoryTable } from '@components/credit-application-history-table/credit-application-history-table';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-history',
  imports: [Header, Subtitle, CreditApplicationHistoryTable],
  templateUrl: './history.html',
  styleUrl: './history.css',
})
export default class History {
  private readonly _route = inject(ActivatedRoute);

  readonly menu: MenuItem[] = [
    { icon: 'pi pi-home', routerLink: '../../../../../' },
    { label: 'Empresas', routerLink: '../../../../' },
    { label: 'Solicitudes de crédito', routerLink: '../../' },
    { label: 'Historial', routerLink: './' },
  ];

  readonly creditId = this._route.snapshot.paramMap.get('creditId')!;
}
