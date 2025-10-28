import { Component, inject } from '@angular/core';
import { Header } from '@components/header/header';
import { MenuItem } from 'primeng/api';
import { Info } from './components/info/info';
import { DetailsStore } from './details-store';
import { Subtitle } from '@components/subtitle/subtitle';
import { Button } from 'primeng/button';
import { KycManagerDialog } from './components/kyc-manager-dialog/kyc-manager-dialog';
import { ActivatedRoute } from '@angular/router';
import { LoadingSpinner } from '@components/loading-spinner/loading-spinner';

@Component({
  selector: 'app-details',
  imports: [Header, Info, Subtitle, Button, KycManagerDialog, LoadingSpinner],
  templateUrl: './details.html',
  styleUrl: './details.css',
  providers: [DetailsStore],
})
export default class Details {
  private readonly _route = inject(ActivatedRoute);
  readonly store = inject(DetailsStore);

  readonly menu: MenuItem[] = [
    { icon: 'pi pi-home', routerLink: '../../' },
    { label: 'Clientes', routerLink: '../' },
    { label: 'Detalles', routerLink: './' },
  ];

  constructor() {
    const userId = this._route.snapshot.paramMap.get('id')!;
    this.store.setUserId(userId);
  }

  async ngOnInit() {
    await this.store.fetch();
  }
}
