import { Component } from '@angular/core';
import { Header } from '@features/operator/components/header/header';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-details',
  imports: [Header],
  templateUrl: './details.html',
  styleUrl: './details.css',
})
export default class Details {
  readonly breadcrumbItems: MenuItem[] = [
    { label: 'Solicitudes de crédito', icon: 'pi pi-home', routerLink: '../' },
    { label: 'Detalles', routerLink: './' },
  ];
}
