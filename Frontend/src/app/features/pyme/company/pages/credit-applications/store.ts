import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { CreditApplicationResponse } from '@core/models/credit-application-model';
import { CreditApplicationApi } from '@core/services/credit-application-api';
import { Status } from '@core/types';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';

type State = {
  companyId: string;
  credits: CreditApplicationResponse[];
  fetchStatus: Status;
  redirect: boolean;
};

const initialState: State = {
  companyId: '',
  credits: [],
  fetchStatus: 'pending',
  redirect: false,
};

export const Store = signalStore(
  withState(initialState),
  withMethods((store, creditApplicationApi = inject(CreditApplicationApi)) => ({
    setCompanyId: (companyId: string) => {
      patchState(store, { companyId });
    },
    fetch: async () => {
      patchState(store, { fetchStatus: 'loading' });

      try {
        const credits = await creditApplicationApi.getAllByCompanyId(store.companyId());

        patchState(store, { fetchStatus: 'success', credits });
      } catch (e) {
        let statusCode: number = 0;

        if (e instanceof HttpErrorResponse) {
          statusCode = e.status;
        }

        patchState(store, { fetchStatus: 'failure', redirect: statusCode === 403 });
      }
    },
  }))
);
