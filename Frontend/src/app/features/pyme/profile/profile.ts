import { Component, effect, inject } from '@angular/core';
import { Title } from '@components/title/title';
import { TokenStorage } from '@core/services/token-storage';
import { ProfileStore } from './profile-store';
import { Subtitle } from '@components/subtitle/subtitle';

@Component({
  selector: 'app-profile',
  imports: [Title, Subtitle],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
  providers: [ProfileStore],
})
export default class Profile {
  readonly store = inject(ProfileStore);
  readonly tokenStorage = inject(TokenStorage);

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
