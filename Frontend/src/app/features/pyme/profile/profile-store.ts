import { computed, inject } from '@angular/core';
import { User } from '@core/models/user-model';
import { TokenStorage } from '@core/services/token-storage';
import { UserApi } from '@core/services/user-api';
import { Status } from '@core/types';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { UserActivePipe } from '@pipes/user-active-pipe';

type State = {
  user: User | null;
  fetchUserStatus: Status;
};

const initialState: State = {
  user: null,
  fetchUserStatus: 'pending',
};

export const ProfileStore = signalStore(
  withState(initialState),
  withComputed((store, tokenUser = inject(TokenStorage).user()!) => ({
    data: computed(() => {
      const user = store.user();

      if (!user) {
        return [];
      }

      const isActive = new UserActivePipe().transform(user.isActive);

      return [
        { name: 'Nombres', value: tokenUser.firstName },
        { name: 'Apellidos', value: tokenUser.lastName },
        { name: 'Nombre de usuario', value: user.username },
        { name: 'Correo electrónico', value: user.email },
        { name: 'Teléfono de contacto', value: user.contact },
        { name: 'Estado', value: isActive },
        { name: 'Registrado', value: user.createdAt },
      ];
    }),
  })),
  withMethods((store, userApi = inject(UserApi)) => ({
    fetchUser: async () => {
      patchState(store, { fetchUserStatus: 'loading' });

      try {
        const user = (await userApi.getMe()).data;
        patchState(store, { fetchUserStatus: 'success', user });
      } catch (e) {
        patchState(store, { fetchUserStatus: 'failure' });
      }
    },
  }))
);
