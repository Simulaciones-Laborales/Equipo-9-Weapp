import { Status } from '@core/types';
import { CreditApplicationResponse } from '@core/models/credit-application-model';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { computed, inject } from '@angular/core';
import { CreditApplicationApi } from '@core/services/credit-application-api';
import { HttpErrorResponse } from '@angular/common/http';

type Show = 'kyc' | 'list';

type State = {
  credits: CreditApplicationResponse[];
  status: Status;
  errorStatus: number | null;
  show: Show;
};

const initialState: State = {
  credits: [],
  status: 'pending',
  errorStatus: null,
  show: 'list',
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
    fetchAll: async (companyId: string) => {
      patchState(store, { status: 'loading' });

      try {
        const credits = await service.getAllByCompanyId(companyId);
        patchState(store, { credits, status: 'success' });
      } catch (e) {
        const errorStatus = e instanceof HttpErrorResponse ? e.status : null;
        patchState(store, { status: 'failure', errorStatus });
      }
    },
    setShow: (show: Show) => {
      patchState(store, { show });
    },
  }))
);
