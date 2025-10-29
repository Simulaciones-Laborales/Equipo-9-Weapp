import { Component, inject } from '@angular/core';
import { Header } from '@components/header/header';
import { MenuItem } from 'primeng/api';
import { CreditApplicationHistoryTable } from '@components/credit-application-history-table/credit-application-history-table';
import { ActivatedRoute } from '@angular/router';
import { Subtitle } from '@components/subtitle/subtitle';

@Component({
  selector: 'app-history',
  imports: [Header, CreditApplicationHistoryTable, Subtitle],
  templateUrl: './history.html',
  styleUrl: './history.css',
})
export default class History {
  private readonly _route = inject(ActivatedRoute);
  readonly creditId = this._route.snapshot.paramMap.get('id')!;

  readonly menu: MenuItem[] = [
    { icon: 'pi pi-home', routerLink: '/operador' },
    { label: 'Solicitudes de crédito', routerLink: '../../' },
    { label: 'Detalles', routerLink: '../' },
    { label: 'Historial', routerLink: './' },
  ];
}
