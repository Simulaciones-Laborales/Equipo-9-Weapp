import { inject } from '@angular/core';
import { User } from '@core/models/user-model';
import { TokenStorage } from '@core/services/token-storage';
import { UserApi } from '@core/services/user-api';
import { Status } from '@core/types';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';

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
  withMethods((store, userApi = inject(UserApi), tokenStorage = inject(TokenStorage)) => ({
    fetchUser: async () => {
      patchState(store, { fetchUserStatus: 'loading' });

      try {
        const user = await userApi.getById(tokenStorage.user()?.id!);
        patchState(store, { fetchUserStatus: 'success', user });
      } catch (e) {
        patchState(store, { fetchUserStatus: 'failure' });
      }
    },
  }))
);
