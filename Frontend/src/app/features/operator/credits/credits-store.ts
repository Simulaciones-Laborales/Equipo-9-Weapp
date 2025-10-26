import { inject } from '@angular/core';
import {
  CreditApplicationResponse,
  CreditApplicationStatus,
} from '@core/models/credit-application-model';
import { CreditApplicationApi } from '@core/services/credit-application-api';
import { Pageable, PageableResponse, Status } from '@core/types';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';

type State = {
  credits: PageableResponse<CreditApplicationResponse> | null;
  status: CreditApplicationStatus | null;
  fetchCreditsStatus: Status;
};

const initialState: State = {
  credits: null,
  status: null,
  fetchCreditsStatus: 'pending',
};

export const CreditsStore = signalStore(
  withState(initialState),
  withMethods((store, creditApplicationsApi = inject(CreditApplicationApi)) => ({
    fetchAll: async (status: CreditApplicationStatus | null, pageable: Pageable) => {
      patchState(store, { fetchCreditsStatus: 'loading', status });

      try {
        const credits = await creditApplicationsApi.getAll(status, pageable);
        patchState(store, { fetchCreditsStatus: 'success', credits });
      } catch (e) {
        patchState(store, { fetchCreditsStatus: 'failure' });
      }
    },
  }))
);
