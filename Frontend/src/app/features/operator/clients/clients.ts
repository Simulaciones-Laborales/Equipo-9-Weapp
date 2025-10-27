import { Component, inject } from '@angular/core';
import { ClientsStore } from './clients-store';
import { Header } from '../components/header/header';
import { MenuItem } from 'primeng/api';
import { ClientsTable } from './components/clients-table/clients-table';
import { Subtitle } from '@components/subtitle/subtitle';
import { LoadingSpinner } from '@components/loading-spinner/loading-spinner';
import { KycManagerDialog } from '../components/kyc-manager-dialog/kyc-manager-dialog';

@Component({
  selector: 'app-clients',
  imports: [Header, ClientsTable, Subtitle, LoadingSpinner, KycManagerDialog],
  templateUrl: './clients.html',
  styleUrl: './clients.css',
  providers: [ClientsStore],
})
export default class Clients {
  readonly menu: MenuItem[] = [
    { icon: 'pi pi-home', routerLink: '../' },
    { label: 'Clientes', routerLink: './' },
  ];

  readonly store = inject(ClientsStore);

  async ngOnInit() {
    await this.store.fetchAll();
  }

  async onSelectVerification(userId: string) {
    await this.store.fetchKyc(userId);
  }
}
