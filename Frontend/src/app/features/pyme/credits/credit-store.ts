import { Status } from '@core/types';
import { CreditApplicationResponse } from '../../../core/models/credit-application-model';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { computed, inject } from '@angular/core';
import { CreditApplicationApi } from '../../../core/services/credit-application-api';

type State = {
  credits: CreditApplicationResponse[];
  status: Status;
};

const initialState: State = {
  credits: [],
  status: 'pending',
};

export const CreditStore = signalStore(
  withState(initialState),
  withComputed((store) => ({
    loadingMessage: computed(() => {
      if (store.status() === 'loading') {
        return 'Cargando mis aplicaciones de crédito...';
      }

      return null;
    }),
  })),
  withMethods((store, service = inject(CreditApplicationApi)) => ({
    fetchAllMyCredits: async () => {
      patchState(store, { status: 'loading' });

      try {
        const credits = await service.getAllMyCreditApplications();
        patchState(store, { credits, status: 'success' });
      } catch (e) {
        patchState(store, { status: 'failure' });
      }
    },
  }))
);
