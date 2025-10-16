import { Status } from '@core/types';
import { CreditApplicationResponse } from '../../../core/models/credit-application-model';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { inject } from '@angular/core';
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
  withMethods((store, service = inject(CreditApplicationApi)) => ({
    fetchAllByCompanyId: async (companyId: string) => {
      patchState(store, { status: 'loading' });

      try {
        const credits = await service.getAllByCompanyId(companyId);
        patchState(store, { credits, status: 'success' });
      } catch (e) {
        patchState(store, { status: 'failure' });
      }
    },
  }))
);
