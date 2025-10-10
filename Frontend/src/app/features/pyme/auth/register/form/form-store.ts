import { inject } from '@angular/core';
import { Status } from '@core/types';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { AuthApi } from '../../services/auth-api';
import { RegisterModel } from '../../models/auth-model';
import { lastValueFrom } from 'rxjs';

type State = {
  status: Status;
  error: string | null;
};

const initialState: State = {
  status: 'pending',
  error: null,
};

export const FormStore = signalStore(
  withState(initialState),
  withMethods((store, service = inject(AuthApi)) => ({
    register: async (data: RegisterModel) => {
      patchState(store, { status: 'loading' });

      try {
        await lastValueFrom(service.register(data));
        patchState(store, { status: 'success', error: null });
      } catch (e) {
        const message = e instanceof Error ? e.message : '';
        patchState(store, { status: 'failure', error: message });
      }
    },
  }))
);
