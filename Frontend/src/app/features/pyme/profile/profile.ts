import { Component, effect, inject } from '@angular/core';
import { TokenStorage } from '@core/services/token-storage';
import { ProfileStore } from './profile-store';
import { Subtitle } from '@components/subtitle/subtitle';
import { Header } from '@components/header/header';
import { MenuItem } from 'primeng/api';
import { LoadingSpinner } from '@components/loading-spinner/loading-spinner';

@Component({
  selector: 'app-profile',
  imports: [Header, Subtitle, LoadingSpinner],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
  providers: [ProfileStore],
})
export default class Profile {
  readonly store = inject(ProfileStore);
  readonly tokenStorage = inject(TokenStorage);

  readonly menu: MenuItem[] = [
    { icon: 'pi pi-home', routerLink: '../' },
    { label: 'Mi Perfil', routerLink: './' },
  ];

  constructor() {
    effect(() => {
      switch (this.store.fetchUserStatus()) {
        case 'success':
          break;
      }
    });
  }

  async ngOnInit() {
    await this.store.fetchUser();
  }
}
