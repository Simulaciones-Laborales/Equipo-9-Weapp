import { inject } from '@angular/core';
import { Status } from '@core/types';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { Api } from './api';
import { DashboardDto } from './model';

type State = {
  status: Status;
  dashboard: DashboardDto | null;
};

const initialState: State = {
  status: 'pending',
  dashboard: null,
};

export const Store = signalStore(
  withState(initialState),
  withMethods((store, api = inject(Api)) => ({
    fetch: async () => {
      patchState(store, { status: 'loading' });

      try {
        const dashboard = (await api.fetch()).data;
        patchState(store, { status: 'success', dashboard });
      } catch (e) {
        patchState(store, { status: 'failure' });
      }
    },
  }))
);
