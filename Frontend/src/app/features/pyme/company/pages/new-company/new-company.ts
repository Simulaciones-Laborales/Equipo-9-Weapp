import { Component } from '@angular/core';
import { Header } from '@components/header/header';
import { Subtitle } from '@components/subtitle/subtitle';
import { MenuItem } from 'primeng/api';
import { NewCompanyForm } from './new-company-form/new-company-form';

@Component({
  selector: 'app-new-company',
  imports: [Header, Subtitle, NewCompanyForm],
  templateUrl: './new-company.html',
  styleUrl: './new-company.css',
})
export default class NewCompany {
  readonly menu: MenuItem[] = [
    { icon: 'pi pi-home', routerLink: '../../' },
    { label: 'Empresas', routerLink: '../' },
    { label: 'Registro Empresa', routerLink: './' },
  ];
}
