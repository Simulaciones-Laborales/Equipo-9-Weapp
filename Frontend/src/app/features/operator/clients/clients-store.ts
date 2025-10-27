import { inject } from '@angular/core';
import { User } from '@core/models/user-model';
import { UserApi } from '@core/services/user-api';
import { Status } from '@core/types';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';

type State = {
  clients: User[];
  fetchStatus: Status;
};

const initialState: State = {
  clients: [],
  fetchStatus: 'pending',
};

export const ClientsStore = signalStore(
  withState(initialState),
  withMethods((store, userApi = inject(UserApi)) => ({
    fetchAll: async () => {
      patchState(store, { fetchStatus: 'loading' });

      try {
        const clients = (await userApi.getAll()).data;
        patchState(store, { fetchStatus: 'success', clients });
      } catch (e) {
        patchState(store, { fetchStatus: 'failure' });
      }
    },
  }))
);
