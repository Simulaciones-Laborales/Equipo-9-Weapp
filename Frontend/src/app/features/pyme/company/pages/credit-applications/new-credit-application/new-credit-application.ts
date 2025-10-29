import { Component } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { Header } from '@components/header/header';
import { Subtitle } from '@components/subtitle/subtitle';
import { Form } from '../form/form';

@Component({
  selector: 'app-new-credit-application',
  imports: [Header, Subtitle, Form],
  templateUrl: './new-credit-application.html',
  styleUrl: './new-credit-application.css',
})
export default class NewCreditApplication {
  readonly menu: MenuItem[] = [
    { icon: 'pi pi-home', routerLink: '../../../../' },
    { label: 'Empresas', routerLink: '../../../' },
    { label: 'Solicitudes de crédito', routerLink: '../' },
    { label: 'Nueva solicitud', routerLink: './' },
  ];
}
