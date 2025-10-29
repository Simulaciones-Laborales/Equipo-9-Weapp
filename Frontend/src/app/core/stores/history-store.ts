import { inject } from '@angular/core';
import { CreditApplicationHistory } from '@core/models/credit-application-model';
import { CreditApplicationApi } from '@core/services/credit-application-api';
import { Pageable, Status } from '@core/types';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';

type State = {
  creditId: string;
  history: CreditApplicationHistory[];
  status: Status;
  pageable: Pageable;
};

const initialState: State = {
  creditId: '',
  history: [],
  status: 'pending',
  pageable: { page: 0, size: 10, sort: ['createdAt'] },
};

export const HistoryStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withMethods((store, creditApplicationApi = inject(CreditApplicationApi)) => ({
    setCreditId: (creditId: string) => {
      patchState(store, { creditId });
    },
    fetch: async () => {
      patchState(store, { status: 'loading' });

      try {
        const history = await creditApplicationApi.getHistory(store.creditId(), store.pageable());

        patchState(store, { status: 'success', history: history.content });
      } catch (e) {
        patchState(store, { status: 'failure' });
      }
    },
  }))
);
