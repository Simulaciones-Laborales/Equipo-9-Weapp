import { Component, inject } from '@angular/core';
import { Header } from '@features/operator/components/header/header';
import { MenuItem } from 'primeng/api';
import { DetailsStore } from './details-store';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-details',
  imports: [Header],
  templateUrl: './details.html',
  styleUrl: './details.css',
  providers: [DetailsStore],
})
export default class Details {
  private readonly _route = inject(ActivatedRoute);
  readonly store = inject(DetailsStore);

  readonly breadcrumbItems: MenuItem[] = [
    { label: 'Solicitudes de crédito', icon: 'pi pi-home', routerLink: '../' },
    { label: 'Detalles', routerLink: './' },
  ];

  async ngOnInit() {
    const id = this._route.snapshot.paramMap.get('id');

    await this.store.fetch(id!);
  }
}
