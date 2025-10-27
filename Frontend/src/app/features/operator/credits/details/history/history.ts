import { Component } from '@angular/core';
import { Header } from '@features/operator/components/header/header';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-history',
  imports: [Header],
  templateUrl: './history.html',
  styleUrl: './history.css',
})
export default class History {
  readonly menu: MenuItem[] = [
    { icon: 'pi pi-home', routerLink: '/operador' },
    { label: 'Solicitudes de crédito', routerLink: '../../' },
    { label: 'Detalles', routerLink: '../' },
    { label: 'Historial', routerLink: './' },
  ];
}
