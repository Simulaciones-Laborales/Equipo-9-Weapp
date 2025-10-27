import { inject } from '@angular/core';
import {
  CreditApplicationResponse,
  CreditApplicationStatus,
  UpdateCreditApplicationStatusDto,
} from '@core/models/credit-application-model';
import { CreditApplicationApi } from '@core/services/credit-application-api';
import { Status } from '@core/types';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';

type State = {
  id: string | null;
  credit: CreditApplicationResponse | null;
  fetchStatus: Status;
  updateStatusStatus: Status;
};

const initialState: State = {
  id: null,
  credit: null,
  fetchStatus: 'pending',
  updateStatusStatus: 'pending',
};

export const DetailsStore = signalStore(
  withState(initialState),
  withMethods((store, creditApplicationApi = inject(CreditApplicationApi)) => ({
    setId: (id: string) => {
      patchState(store, { id });
    },
    fetch: async () => {
      patchState(store, { fetchStatus: 'loading' });

      try {
        const credit = await creditApplicationApi.getById(store.id()!);
        patchState(store, { fetchStatus: 'success', credit });
      } catch (e) {
        patchState(store, { fetchStatus: 'failure' });
      }
    },
    updateStatus: async (dto: UpdateCreditApplicationStatusDto) => {
      patchState(store, { updateStatusStatus: 'loading' });

      try {
        const credit = await creditApplicationApi.updateStatus(store.id()!, dto);
        patchState(store, { updateStatusStatus: 'success', credit });
      } catch (e) {
        patchState(store, { updateStatusStatus: 'failure' });
      }
    },
  }))
);
