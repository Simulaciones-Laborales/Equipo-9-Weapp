import { Status } from '@core/types';
import { CompanyResponse } from '../../models/company-model';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { inject } from '@angular/core';
import { CompanyApi } from '../../services/company-api';

type State = {
  companies: CompanyResponse[];
  status: Status;
};

const initialState: State = {
  companies: [],
  status: 'pending',
};

export const ListStore = signalStore(
  withState(initialState),
  withMethods((store, companyApi = inject(CompanyApi)) => ({
    fetch: async () => {
      patchState(store, { status: 'loading' });

      try {
        const companies = await companyApi.getAllByAuthenticatedUser();
        patchState(store, { status: 'success', companies });
      } catch (e) {
        patchState(store, { status: 'failure' });
      }
    },
  }))
);
