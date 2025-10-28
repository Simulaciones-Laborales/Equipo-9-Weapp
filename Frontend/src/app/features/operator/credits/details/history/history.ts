import { Component, inject } from '@angular/core';
import { Header } from '@components/header/header';
import { MenuItem } from 'primeng/api';
import { CreditApplicationsTable } from '@components/credit-applications-table/credit-applications-table';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-history',
  imports: [Header, CreditApplicationsTable],
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
