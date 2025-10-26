import { inject } from '@angular/core';
import { CreditApplicationResponse } from '@core/models/credit-application-model';
import { CreditApplicationApi } from '@core/services/credit-application-api';
import { Status } from '@core/types';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';

type State = {
  credit: CreditApplicationResponse | null;
  fetchStatus: Status;
};

const initialState: State = {
  credit: null,
  fetchStatus: 'pending',
};

export const DetailsStore = signalStore(
  withState(initialState),
  withMethods((store, creditApplicationApi = inject(CreditApplicationApi)) => ({
    fetch: async (id: string) => {
      patchState(store, { fetchStatus: 'loading' });

      try {
        const credit = await creditApplicationApi.getById(id);
        patchState(store, { fetchStatus: 'success', credit });
      } catch (e) {
        patchState(store, { fetchStatus: 'failure' });
      }
    },
  }))
);
