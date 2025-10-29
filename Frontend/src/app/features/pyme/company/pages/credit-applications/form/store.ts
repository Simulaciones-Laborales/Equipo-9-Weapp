import { inject } from '@angular/core';
import { CreateCreditApplicationDto } from '@core/models/credit-application-model';
import { CreditApplicationApi } from '@core/services/credit-application-api';
import { Status } from '@core/types';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';

type State = {
  status: Status;
};

const initialState: State = {
  status: 'pending',
};

export const Store = signalStore(
  withState(initialState),
  withMethods((store, creditApplicationApi = inject(CreditApplicationApi)) => ({
    create: async (data: CreateCreditApplicationDto, files: File[]) => {
      patchState(store, { status: 'loading' });

      try {
        await creditApplicationApi.create(data, files);
        patchState(store, { status: 'success' });
      } catch (e) {
        patchState(store, { status: 'failure' });
      }
    },
  }))
);
