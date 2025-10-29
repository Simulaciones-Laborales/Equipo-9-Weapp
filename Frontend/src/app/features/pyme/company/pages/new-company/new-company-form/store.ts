import { computed, inject } from '@angular/core';
import { Status } from '@core/types';
import { CompanyRequest } from '@features/pyme/company/models/company-model';
import { CompanyApi } from '@features/pyme/company/services/company-api';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';

type State = {
  status: Status;
};

const initialState: State = {
  status: 'pending',
};

export const Store = signalStore(
  withState(initialState),
  withComputed((store) => ({
    loadingMessage: computed(() =>
      store.status() === 'loading' ? 'Registrando tu Empresa...' : 'Completar Registro'
    ),
  })),
  withMethods((store, companyApi = inject(CompanyApi)) => ({
    createCompany: async (req: CompanyRequest) => {
      patchState(store, { status: 'loading' });

      try {
        await companyApi.create(req);

        patchState(store, { status: 'success' });
      } catch (e) {
        patchState(store, { status: 'failure' });
      }
    },
  }))
);
